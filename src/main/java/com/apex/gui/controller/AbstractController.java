package com.apex.gui.controller;

import com.apex.core.Constants;
import com.apex.core.RuntimeStates;
import com.apex.gui.util.ErrorDialogRenderer;
import com.apex.gui.util.GuiElementsBuilder;
import com.apex.io.textureloader.TextureLoader;
import com.apex.io.util.IOProcessParams;
import com.apex.io.util.IOProcessor;
import com.apex.io.write.ObjWriter;
import com.apex.math.Vector3f;
import com.apex.model.geometry.Model;
import com.apex.model.scene.AssociationBuffer;
import com.apex.model.scene.Camera;
import com.apex.model.scene.RenderObject;
import com.apex.model.util.RenderObjectStatus;
import com.apex.reflection.AutoCreation;
import com.apex.reflection.AutoInject;
import com.apex.render.RenderEngine;
import com.apex.render.pipeline.PipelineConfigurer;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;
import com.apex.storage.collision.CollisionManager;
import com.apex.storage.transformation.TransformationController;
import com.apex.tool.ModelTools;
import com.apex.util.ActiveCameraWrapper;
import com.apex.util.ColorUtil;
import com.apex.util.IndexParser;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;

import com.apex.io.read.ObjReader;
import javafx.stage.Stage;

public abstract class AbstractController implements Controller {
    @AutoInject
    protected TransformationController transformationController;
    @AutoInject(name = "ReadIOProcessor")
    protected IOProcessor inputProcessor;
    @AutoInject(name = "WriteIOProcessor")
    protected IOProcessor writeProcessor;
    @AutoInject
    protected SceneStorage sceneStorage;
    @AutoInject
    protected RenderEngine renderEngine;
    @AutoInject
    protected ActiveCameraWrapper activeCameraWrapper;
    @AutoInject
    protected CollisionManager collisionManager;
    @AutoInject
    protected CameraStorage cameraStorage;
    @AutoInject
    protected PipelineConfigurer pipelineConfigurer;
    @AutoInject
    protected RuntimeStates runtimeStates;
    @AutoInject
    protected AssociationBuffer associationBuffer;

    @FunctionalInterface
    protected interface ThrowableRunnable {
        void run() throws Exception;
    }

    protected void executeSafe(String context, ThrowableRunnable action) {
        startOperation();
        try {
            action.run();
        } catch (Exception e) {
            com.apex.gui.util.ErrorDialogRenderer.showError("Error during " + context, e);
        } finally {
            endOperation();
        }
    }

    private long lastRenderTime = 0;
    private final long RENDER_INTERVAL = 45;

    @FXML
    protected Canvas canvas;
    @FXML
    protected ImageView imageView;
    @FXML
    protected AnchorPane rootPane;
    @FXML
    protected ScrollPane rightPane;
    @FXML
    protected Pane renderPane;
    @FXML
    protected VBox notificationContainer;
    @FXML
    protected VBox modelsVBox;
    @FXML
    protected HBox camerasHeader;
    @FXML
    protected VBox camerasVBox;
    @FXML
    protected Button camerasCollapseBtn;

    // Modification UI elements
    @FXML
    protected VBox modificationContentVBox;
    @FXML
    protected Button modificationCollapseBtn;
    @FXML
    protected TextField vertexIndicesField;

    @AutoInject
    protected com.apex.modification.VertexRemover vertexRemover;

    // Affine transformation UI elements
    @FXML
    protected VBox affineContentVBox;
    @FXML
    protected Button affineCollapseBtn;
    @FXML
    protected TextField scaleXField, scaleYField, scaleZField;
    @FXML
    protected TextField rotateXField, rotateYField, rotateZField;
    @FXML
    protected TextField translateXField, translateYField, translateZField;
    @FXML
    protected TextField polygonIndicesField;
    @FXML
    protected Button affineApplyBtn, affineResetBtn;

    // Rendering Modes UI elements
    @FXML
    protected VBox renderingModesContentVBox;
    @FXML
    protected Button renderingModesCollapseBtn;
    @FXML
    protected CheckBox wireframeCheckBox;
    @FXML
    protected CheckBox texturesCheckBox;
    @FXML
    protected CheckBox lightingCheckBox;

    // Settings UI elements
    @FXML
    protected VBox settingsContentVBox;
    @FXML
    protected Button settingsCollapseBtn;
    @FXML
    protected ColorPicker colorPicker;

    private double lastMouseX = 0;
    private double lastMouseY = 0;
    private boolean isMousePressed = false;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ErrorDialogRenderer.setNotificationContainer(notificationContainer);
        imageView.setPreserveRatio(false);

        // Link imageView size to renderPane
        imageView.fitWidthProperty().bind(renderPane.widthProperty());
        imageView.fitHeightProperty().bind(renderPane.heightProperty());

        renderPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                runtimeStates.SCENE_WIDTH = newVal.intValue();
                refreshBuffer(runtimeStates.SCENE_WIDTH, runtimeStates.SCENE_HEIGHT);
                refreshAssociationBuffer();
                refreshRender();
            }
        });

        renderPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                runtimeStates.SCENE_HEIGHT = newVal.intValue();
                refreshBuffer(runtimeStates.SCENE_WIDTH, runtimeStates.SCENE_HEIGHT);
                refreshAssociationBuffer();
                refreshRender();
            }
        });

        if (rootPane.getScene() != null) {
            setupGlobalKeyHandling();
        } else {
            rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    setupGlobalKeyHandling();
                }
            });
        }

        setupMouseHandlers();

        // Collapse specific sections by default (except lists/cameras if desired)
        // User requested: "all except camera list were collapsed"
        collapseSection(affineContentVBox, affineCollapseBtn);
        collapseSection(renderingModesContentVBox, renderingModesCollapseBtn);
        collapseSection(modificationContentVBox, modificationCollapseBtn);
        collapseSection(settingsContentVBox, settingsCollapseBtn);

        Platform.runLater(() -> {
            if (renderPane.getWidth() > 0)
                runtimeStates.SCENE_WIDTH = (int) renderPane.getWidth();
            if (renderPane.getHeight() > 0)
                runtimeStates.SCENE_HEIGHT = (int) renderPane.getHeight();
            refreshBuffer(runtimeStates.SCENE_WIDTH, runtimeStates.SCENE_HEIGHT);
            refreshGui();
        });
    }

    private void handleModelPartChoose(MouseEvent event) {
        if (event.isControlDown()) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int vertexIndex = associationBuffer.getVertexIndex(x, y);
            if (vertexIndex == -1) return;
            String roName = associationBuffer.getModelFilename(x, y);
            RenderObject ro = sceneStorage.getRenderObject(roName);
            if (ro.getSelectedVertexIndices().contains(vertexIndex)) {
                ro.getSelectedVertexIndices().remove(vertexIndex);

            } else
                ro.getSelectedVertexIndices().add(vertexIndex);
        }
        if (event.isShiftDown()) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int polygonIndex = associationBuffer.getPolygonIndex(x, y);
            if (polygonIndex == -1) return;
            String roName = associationBuffer.getModelFilename(x, y);
            RenderObject ro = sceneStorage.getRenderObject(roName);
            if (ro.getSelectedPolygonIndices().contains(polygonIndex)) {
                ro.getSelectedPolygonIndices().remove(polygonIndex);
            } else
                ro.getSelectedPolygonIndices().add(polygonIndex);
        }
        refreshRender();
    }

    private void setupMouseHandlers() {
        // Обработка вращения камеры (левая кнопка мыши)
        renderPane.setOnMousePressed(event -> {
            if (event.isSecondaryButtonDown()) handleModelPartChoose(event);
            if (event.isPrimaryButtonDown()) {
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
                isMousePressed = true;
                renderPane.getScene().setCursor(javafx.scene.Cursor.CLOSED_HAND);
            }
        });


        renderPane.setOnMouseDragged(event -> {
            if (isMousePressed && event.isPrimaryButtonDown()) {
                double currentX = event.getSceneX();
                double currentY = event.getSceneY();

                double deltaX = currentX - lastMouseX;
                double deltaY = currentY - lastMouseY;

                lastMouseX = currentX;
                lastMouseY = currentY;

                // определяем тип управления
                if (event.isControlDown()) {
                    transformationController.panCamera(new Vector3f((float) deltaX, (float) deltaY, 0));
                } else {
                    transformationController.moveCameraOnVector(new Vector3f((float) deltaX, (float) deltaY, 0));
                }
                long now = System.currentTimeMillis();
                if (now - lastRenderTime >= RENDER_INTERVAL) {
                    refreshRender();
                    lastRenderTime = now;
                }
            }
        });

        renderPane.setOnMouseReleased(event -> {
            isMousePressed = false;
            if (renderPane.getScene() != null) {
                renderPane.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });

        // обработка зума (на колесико мыши)
        renderPane.setOnScroll(event -> {
            double delta = event.getDeltaY();
            if (delta != 0) {
                float zoomSpeed = 0.1f;
                transformationController.zoomCamera((float) delta * zoomSpeed);
                long now = System.currentTimeMillis();
                if (now - lastRenderTime >= RENDER_INTERVAL) {
                    refreshRender();
                    lastRenderTime = now;
                }
            }
        });
    }

    private void setupGlobalKeyHandling() {
        Scene scene = rootPane.getScene();
        if (scene == null)
            return;

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            boolean handled = true;

            // Handle Ctrl+F for Finding models (or whatever it was)
            if (event.isControlDown() && event.getCode() == KeyCode.F) {
                onOpenModelHandler(null);
                event.consume();
                return;
            }

            switch (event.getCode()) {
                case W -> handleCameraForward(new ActionEvent());
                case S -> handleCameraBackward(new ActionEvent());
                case A -> handleCameraLeft(new ActionEvent());
                case D -> handleCameraRight(new ActionEvent());
                case Q -> handleCameraUp(new ActionEvent());
                case E -> handleCameraDown(new ActionEvent());
                case UP -> handleCameraRotateUp(new ActionEvent());
                case DOWN -> handleCameraRotateDown(new ActionEvent());
                case LEFT -> handleCameraRotateLeft(new ActionEvent());
                case RIGHT -> handleCameraRotateRight(new ActionEvent());
                default -> handled = false;
            }
            if (handled) {
                event.consume();
            }
        });
    }

    @FXML
    public void handleCameraRotateLeft(ActionEvent actionEvent) {
        transformationController.rotateCamera(-Constants.ROTATION_SPEED, 0);
        refreshRender();
    }

    @FXML
    public void handleCameraRotateRight(ActionEvent actionEvent) {
        transformationController.rotateCamera(Constants.ROTATION_SPEED, 0);
        refreshRender();
    }

    @FXML
    public void handleCameraRotateUp(ActionEvent actionEvent) {
        transformationController.rotateCamera(0, -Constants.ROTATION_SPEED);
        refreshRender();
    }

    @FXML
    public void handleCameraRotateDown(ActionEvent actionEvent) {
        transformationController.rotateCamera(0, Constants.ROTATION_SPEED);
        refreshRender();
    }

    @FXML
    public void handleWireframeToggle() {
        if (wireframeCheckBox.isSelected()) {
            pipelineConfigurer.enableFirstReserved();
        } else {
            pipelineConfigurer.disableLast();
        }
        refreshRender();
    }

    @FXML
    public void handleTexturesToggle() {
        if (texturesCheckBox.isSelected()) {
            sceneStorage.onTextures();
        } else {
            sceneStorage.offTextures();
        }
        refreshRender();
    }

    @FXML
    public void handleLightingToggle() {
        if (lightingCheckBox.isSelected()) {
            sceneStorage.enableLightingForAll();
        } else {
            sceneStorage.disableLightingForAll();
        }
        refreshRender();
    }

    @FXML
    public void handleRenderPaneClick(MouseEvent event) {
        // Click handler for render pane - can be used for future interactions
    }

    @FXML
    public void handleScrollPaneKeyPressed(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.UP ||
                event.getCode() == javafx.scene.input.KeyCode.DOWN ||
                event.getCode() == javafx.scene.input.KeyCode.LEFT ||
                event.getCode() == javafx.scene.input.KeyCode.RIGHT) {
            event.consume();
        }
    }

    @FXML
    @Override
    public void onOpenModelHandler(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null)
            return;

        executeSafe("opening model: " + file.getName(), () -> {
            Path fileName = Path.of(file.getAbsolutePath());
            String fileContent = Files.readString(fileName);
            Model model = ObjReader.read(fileContent);
            inputProcessor.process(new IOProcessParams(model, IOProcessParams.IOType.INPUT, null, null));
            sceneStorage.addModel(file.getName(), model);
            refreshGui();
            refreshRender();
        });
    }

    @Override
    public void onDeleteModelHandler(String filename) {
        executeSafe("deleting model: " + filename, () -> {
            sceneStorage.deleteModel(filename);
            refreshGui();
        });
    }

    @FXML
    @Override
    public void onSaveModelHandler(ActionEvent event) {
        executeSafe("saving models", () -> {
            Stage stage = GuiElementsBuilder.createModelChooseWindow(new ArrayList<>(sceneStorage.getRenderObjects()),
                    this::onSaveModel, this::handleChoosingFolder);
            stage.showAndWait();
        });
    }

    private String handleChoosingFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Directory to save");
        File file = directoryChooser.showDialog(rootPane.getScene().getWindow());
        if (file == null)
            return "";
        return file.getPath();
    }

    private void onSaveModel(String path, String s, boolean saveTransformed) throws IOException {
        RenderObject ro = sceneStorage.getRenderObject(s);
        Model modelToSave = ro.getModel().copy();
        writeProcessor.process(
                new IOProcessParams(modelToSave, IOProcessParams.IOType.OUTPUT, saveTransformed, ro.getWorldMatrix()));
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        ObjWriter.write(modelToSave, Paths.get(path, timestamp + "_" + ro.getFilename()).toString());
    }

    @Override
    public void onAddTextureHandler(String filename) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file == null)
            return;

        executeSafe("adding texture to " + filename, () -> {
            Image image = TextureLoader.loadTextureFromFile(file);
            sceneStorage.addTexture(filename, file.getName(), image);
            refreshGui();
        });
    }

    @Override
    public void onRemoveTextureHandler(String filename) {
        executeSafe("removing texture from " + filename, () -> {
            sceneStorage.deleteTexture(filename);
            refreshGui();
        });
    }

    @Override
    public void onChangeVisibilityHandler(String filename) {
        executeSafe("changing visibility for " + filename, () -> {
            RenderObject renderObject = sceneStorage.getRenderObject(filename);
            if (renderObject.isVisible())
                sceneStorage.makeUnVisible(filename);
            else
                sceneStorage.makeVisible(filename);
            refreshGui();
        });
    }

    @Override
    public void onChangeActiveStatusHandler(String filename) {
        executeSafe("changing active status for " + filename, () -> {
            RenderObject renderObject = sceneStorage.getRenderObject(filename);
            if (renderObject.getStatus().equals(RenderObjectStatus.ACTIVE))
                sceneStorage.makeUnactive(filename);
            else
                sceneStorage.makeActive(filename);
            refreshGui();
        });
    }

    @Override
    public void refreshGui() {
        refreshModelList();
        refreshCameraList();
    }

    private void refreshModelList() {
        modelsVBox.getChildren().clear();
        for (RenderObject renderObject : sceneStorage.getRenderObjects()) {
            Node node = GuiElementsBuilder.createObjectNode(
                    renderObject,
                    this::onDeleteModelHandler,
                    this::onAddTextureHandler,
                    this::onRemoveTextureHandler,
                    this::onChangeVisibilityHandler,
                    this::onChangeActiveStatusHandler,
                    this::focusOnObject
            );
            modelsVBox.getChildren().add(node);
        }
    }


    private void focusOnObject(String name) {
        executeSafe("focusing camera on " + name, () -> {
            RenderObject ro = sceneStorage.getRenderObject(name);
            if (ro == null) return;

            Vector3f center = ro.getWorldCenter();

            Camera camera = activeCameraWrapper.getActiveCamera();
            camera.setTarget(center);

            refreshRender();
        });
    }




    private void refreshCameraList() {
        if (camerasVBox == null)
            return;
        camerasVBox.getChildren().clear();
        String activeCameraName = cameraStorage.getActiveCameraName();
        for (String name : cameraStorage.getCamerasNames()) {
            boolean isActive = name.equals(activeCameraName);
            Node node = GuiElementsBuilder.createCameraNode(
                    name,
                    isActive,
                    this::onChangeActiveCameraHandler,
                    this::onDeleteCameraHandler);
            camerasVBox.getChildren().add(node);
        }
    }

    @FXML
    public void handleSaveCamera(ActionEvent event) {
        executeSafe("saving current view", () -> {
            Camera active = activeCameraWrapper.getActiveCamera();
            Camera snapshot = active.copy();

            // Generate a unique name
            int count = cameraStorage.getCamerasNames().size();
            String name = "Camera " + count;
            while (cameraStorage.hasCamera(name)) {
                count++;
                name = "Camera " + count;
            }

            cameraStorage.addCamera(name, snapshot);
            refreshCameraList();
        });
        // Prevent collapsing section when clicking save
        if (event != null)
            event.consume();
    }

    private void onDeleteCameraHandler(String name) {
        executeSafe("deleting camera: " + name, () -> {
            cameraStorage.deleteCamera(name);
            refreshCameraList();
            refreshRender();
        });
    }

    private void onChangeActiveCameraHandler(String name) {
        executeSafe("switching to camera: " + name, () -> {
            cameraStorage.setActiveCamera(name);
            refreshCameraList();
            refreshRender();
        });
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        transformationController.moveCameraForward(Constants.TRANSLATION);
        refreshRender();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        transformationController.moveCameraForward(-Constants.TRANSLATION);
        refreshRender();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        transformationController.moveCameraRight(-Constants.TRANSLATION);
        refreshRender();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        transformationController.moveCameraRight(Constants.TRANSLATION);
        refreshRender();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        transformationController.moveCameraUp(Constants.TRANSLATION);
        refreshRender();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        transformationController.moveCameraUp(-Constants.TRANSLATION);
        refreshRender();
    }

    @FXML
    @Override
    public void handleBaseTextureColorChange(ActionEvent event) {
        executeSafe("changing base color", () -> {
            Color color = colorPicker.getValue();
            runtimeStates.color = ColorUtil.toARGB(color);
            sceneStorage.updateColors();
            refreshGui();
        });
    }

    @FXML
    public void handleAffineApply(ActionEvent event) {
        executeSafe("applying transformations", () -> {
            float scaleX = Float.parseFloat(scaleXField.getText());
            float scaleY = Float.parseFloat(scaleYField.getText());
            float scaleZ = Float.parseFloat(scaleZField.getText());

            float rotateX = Float.parseFloat(rotateXField.getText());
            float rotateY = Float.parseFloat(rotateYField.getText());
            float rotateZ = Float.parseFloat(rotateZField.getText());

            float translateX = Float.parseFloat(translateXField.getText());
            float translateY = Float.parseFloat(translateYField.getText());
            float translateZ = Float.parseFloat(translateZField.getText());

            transformationController.updateWorldMatrixForActiveObjects(
                    scaleX, scaleY, scaleZ,
                    rotateX, rotateY, rotateZ,
                    translateX, translateY, translateZ);
            refreshRender();
        });
    }

    @FXML
    public void handleAffineReset(ActionEvent event) {
        scaleXField.setText("1.0");
        scaleYField.setText("1.0");
        scaleZField.setText("1.0");

        rotateXField.setText("0.0");
        rotateYField.setText("0.0");
        rotateZField.setText("0.0");

        translateXField.setText("0.0");
        translateYField.setText("0.0");
        translateZField.setText("0.0");

        handleAffineApply(event);
    }

    @FXML
    public void handleModificationCollapseToggle(MouseEvent event) {
        toggleCollapse(modificationContentVBox, modificationCollapseBtn);
    }

    @FXML
    public void handleCamerasCollapseToggle(MouseEvent event) {
        toggleCollapse(camerasVBox, camerasCollapseBtn);
    }

    @FXML
    public void handleRenderingModesCollapseToggle(MouseEvent event) {
        toggleCollapse(renderingModesContentVBox, renderingModesCollapseBtn);
    }

    @FXML
    public void handleAffineCollapseToggle(MouseEvent event) {
        toggleCollapse(affineContentVBox, affineCollapseBtn);
    }

    @FXML
    public void handleSettingsCollapseToggle(MouseEvent event) {
        toggleCollapse(settingsContentVBox, settingsCollapseBtn);
    }

    protected void toggleCollapse(VBox content, Button toggleBtn) {
        if (content.isVisible()) {
            collapseSection(content, toggleBtn);
        } else {
            expandSection(content, toggleBtn);
        }
    }

    protected void collapseSection(VBox content, Button toggleBtn) {
        content.setVisible(false);
        content.setManaged(false);
        toggleBtn.setText("▼");
        toggleBtn.setRotate(-90);
    }

    protected void expandSection(VBox content, Button toggleBtn) {
        content.setVisible(true);
        content.setManaged(true);
        toggleBtn.setText("▼");
        toggleBtn.setRotate(0);
    }

    @FXML
    public void handleSelectVertices(ActionEvent event) {
        String text = vertexIndicesField.getText();
        java.util.Set<Integer> indices = com.apex.util.IndexParser.parseIndices(text);

        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            ro.setSelectedVertexIndices(indices);
        }
        refreshRender();
    }

    @FXML
    public void handleRemoveVertices(ActionEvent event) {
        String text = vertexIndicesField.getText();

        boolean changed = false;
        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            for (Integer removableIndex : ro.getSelectedVertexIndices()) {
                ModelTools.removeVertex(ro.getModel(), removableIndex);
            }
            inputProcessor.process(new IOProcessParams(ro.getModel(), IOProcessParams.IOType.INPUT, null, null));
            ro.setWorkVertices(new float[ro.getModel().vertices.size() * 4]);
            ro.setSelectedVertexIndices(new java.util.HashSet<>());
            ro.setSelectedPolygonIndices(new java.util.HashSet<>());
            changed = true;
        }

        if (changed) {
            refreshRender();
        }
    }

    @FXML
    public void handleSelectPolygons(ActionEvent event) {
        String text = polygonIndicesField.getText();
        java.util.Set<Integer> indices = com.apex.util.IndexParser.parseIndices(text);

        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            ro.setSelectedPolygonIndices(indices);
        }
        refreshRender();
    }

    @FXML
    public void handleRemovePolygons(ActionEvent event) {
        String text = polygonIndicesField.getText();

        boolean changed = false;
        for (RenderObject ro : sceneStorage.getActiveRenderObjects()) {
            for (Integer removablePolygonIndex : ro.getSelectedPolygonIndices()) {
                ModelTools.removePolygon(ro.getModel(), removablePolygonIndex);
            }
            inputProcessor.process(new IOProcessParams(ro.getModel(), IOProcessParams.IOType.INPUT, null, null));
            ro.setWorkVertices(new float[ro.getModel().vertices.size() * 4]);
            ro.setSelectedVertexIndices(new java.util.HashSet<>());
            ro.setSelectedPolygonIndices(new java.util.HashSet<>());
            changed = true;
        }

        if (changed) {
            refreshRender();
        }
    }

    @Override
    public void refreshAssociationBuffer() {
        associationBuffer.update(runtimeStates.SCENE_WIDTH, runtimeStates.SCENE_HEIGHT);
    }
}
