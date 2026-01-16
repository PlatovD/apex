package com.apex.gui.controller;

import com.apex.core.Constants;
import com.apex.gui.util.GuiElementsBuilder;
import com.apex.io.textureloader.TextureLoader;
import com.apex.io.util.IOProcessor;
import com.apex.math.Vector3f;
import com.apex.model.geometry.Model;
import com.apex.model.scene.RenderObject;
import com.apex.model.util.RenderObjectStatus;
import com.apex.reflection.AutoInject;
import com.apex.render.RenderEngine;
import com.apex.render.pipeline.PipelineConfigurer;
import com.apex.storage.CameraStorage;
import com.apex.storage.SceneStorage;
import com.apex.storage.collision.CollisionManager;
import com.apex.storage.transformation.TransformationController;
import com.apex.util.ActiveCameraWrapper;
import com.apex.util.ColorUtil;
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
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.apex.io.read.ObjReader;

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
    protected VBox modelsVBox;
    @FXML
    protected HBox camerasHeader;
    @FXML
    protected VBox camerasVBox;
    @FXML
    protected Button camerasCollapseBtn;

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
        imageView.setPreserveRatio(false);

        // Link imageView size to renderPane
        imageView.fitWidthProperty().bind(renderPane.widthProperty());
        imageView.fitHeightProperty().bind(renderPane.heightProperty());

        renderPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                Constants.SCENE_WIDTH = newVal.intValue();
                refreshBuffer(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
            }
        });

        renderPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                Constants.SCENE_HEIGHT = newVal.intValue();
                refreshBuffer(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
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

        wireframeCheckBox.setOnAction((actionEvent) -> {
            if (wireframeCheckBox.isSelected()) pipelineConfigurer.enableFirstReserved();
            else pipelineConfigurer.disableLast();
            refreshRender();
        });
        wireframeCheckBox.fire();

        Platform.runLater(() -> {
            if (renderPane.getWidth() > 0)
                Constants.SCENE_WIDTH = (int) renderPane.getWidth();
            if (renderPane.getHeight() > 0)
                Constants.SCENE_HEIGHT = (int) renderPane.getHeight();
            refreshBuffer(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
            refreshGui();
        });
    }

    private void setupMouseHandlers() {
        // Обработка вращения камеры (левая кнопка мыши)
        renderPane.setOnMousePressed(event -> {
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

                // определяем тип управления в зависимости от модификаторов
                if (event.isControlDown()) {
                    // ctrl + ЛКМ = панорамирование
                    transformationController.panCamera(new Vector3f((float) deltaX, (float) deltaY, 0));
                } else {
                    // просто ЛКМ = вращение камеры
                    transformationController.moveCameraOnVector(new Vector3f((float) deltaX, (float) deltaY, 0));
                }

                refreshRender();
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
                refreshRender();
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
                case W -> handleCameraUp(new ActionEvent());
                case S -> handleCameraDown(new ActionEvent());
                case A -> handleCameraLeft(new ActionEvent());
                case D -> handleCameraRight(new ActionEvent());
                case UP -> handleCameraForward(new ActionEvent());
                case DOWN -> handleCameraBackward(new ActionEvent());
                // LEFT and RIGHT are already handled by A and D inside camera but here it
                // matches the user's specific request
                case LEFT -> handleCameraLeft(new ActionEvent());
                case RIGHT -> handleCameraRight(new ActionEvent());
                default -> handled = false;
            }
            if (handled) {
                event.consume();
            }
        });
    }

    @FXML
    public void handleAffineCollapseToggle(MouseEvent event) {
        toggleSection(affineContentVBox, affineCollapseBtn);
    }

    @FXML
    public void handleCamerasCollapseToggle(MouseEvent event) {
        toggleSection(camerasVBox, camerasCollapseBtn);
    }

    @FXML
    public void handleRenderingModesCollapseToggle(MouseEvent event) {
        toggleSection(renderingModesContentVBox, renderingModesCollapseBtn);
    }

    @FXML
    public void handleSettingsCollapseToggle(MouseEvent event) {
        toggleSection(settingsContentVBox, settingsCollapseBtn);
    }

    private void toggleSection(VBox content, Button indicator) {
        boolean isVisible = content.isVisible();
        content.setVisible(!isVisible);
        content.setManaged(!isVisible);
        indicator.setText(!isVisible ? "▼" : "▶");
    }

    @FXML
    public void handleWireframeToggle() {
        if (wireframeCheckBox.isSelected()) {
            sceneStorage.enableWireframeForAll();
        } else {
            sceneStorage.disableWireframeAll();
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
        startOperation();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) {
            endOperation();
            return;
        }

        try {
            Path fileName = Path.of(file.getAbsolutePath());
            String fileContent = Files.readString(fileName);
            Model model = ObjReader.read(fileContent);
            inputProcessor.process(model);
            sceneStorage.addModel(file.getName(), model);
            refreshGui();
            refreshRender();
        } catch (IOException exception) {
            // todo: обработка ошибок
        }
        endOperation();
    }

    @Override
    public void onDeleteModelHandler(String filename) {
        startOperation();

        sceneStorage.deleteModel(filename);
        refreshGui();

        endOperation();
    }

    @FXML
    @Override
    public void onSaveModelHandler(ActionEvent event) {

    }

    @Override
    public void onAddTextureHandler(String filename) {
        startOperation();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) {
            endOperation();
            return;
        }

        try {
            Image image = TextureLoader.loadTextureFromFile(file);
            sceneStorage.addTexture(filename, file.getName(), image);
            refreshGui();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        endOperation();
    }

    @Override
    public void onRemoveTextureHandler(String filename) {
        startOperation();

        sceneStorage.deleteTexture(filename);
        refreshGui();

        endOperation();
    }

    @Override
    public void onChangeVisibilityHandler(String filename) {
        startOperation();

        RenderObject renderObject = sceneStorage.getRenderObject(filename);
        if (renderObject.isVisible())
            sceneStorage.makeUnVisible(filename);
        else
            sceneStorage.makeVisible(filename);
        refreshGui();

        endOperation();
    }

    @Override
    public void onChangeActiveStatusHandler(String filename) {
        startOperation();

        RenderObject renderObject = sceneStorage.getRenderObject(filename);
        if (renderObject.getStatus().equals(RenderObjectStatus.ACTIVE))
            sceneStorage.makeUnactive(filename);
        else
            sceneStorage.makeActive(filename);
        refreshGui();

        endOperation();
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
                    this::onChangeActiveStatusHandler);
            modelsVBox.getChildren().add(node);
        }
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

    private void onDeleteCameraHandler(String name) {
        startOperation();
        cameraStorage.deleteCamera(name);
        refreshCameraList();
        refreshRender();
        endOperation();
    }

    private void onChangeActiveCameraHandler(String name) {
        startOperation();
        cameraStorage.setActiveCamera(name);
        refreshCameraList();
        refreshRender();
        endOperation();
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
        startOperation();
        Color color = colorPicker.getValue();
        Constants.color = ColorUtil.toARGB(color);
        sceneStorage.updateColors();
        refreshGui();
        endOperation();
    }

    @FXML
    public void handleAffineApply(ActionEvent event) {
        startOperation();
        try {
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

        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in transformation fields: " + e.getMessage());
        }
        endOperation();
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
}
