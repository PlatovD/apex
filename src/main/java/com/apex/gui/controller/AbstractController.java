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
import com.apex.storage.SceneStorage;
import com.apex.storage.transformation.TransformationController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import com.apex.io.read.ObjReader;
import com.apex.model.scene.Camera;

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
    protected Camera camera;

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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageView.setPreserveRatio(false);
        renderPane.prefWidthProperty().bind(
                rootPane.widthProperty().subtract(rightPane.widthProperty())
        );

        renderPane.prefHeightProperty().bind(
                rootPane.heightProperty()
        );

        imageView.fitWidthProperty().bind(renderPane.widthProperty());
        imageView.fitHeightProperty().bind(renderPane.heightProperty());

        imageView.fitWidthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                Constants.SCENE_WIDTH = newVal.intValue();
            }
            refreshBuffer(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
        });

        imageView.fitHeightProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() > 0) {
                Constants.SCENE_HEIGHT = newVal.intValue();
            }
            refreshBuffer(Constants.SCENE_WIDTH, Constants.SCENE_HEIGHT);
        });
        Platform.runLater(this::setupKeyBindings);
    }

    private void setupKeyBindings() {
        Scene scene = rootPane.getScene();

        KeyCombination wKey = new KeyCodeCombination(KeyCode.W);
        KeyCombination sKey = new KeyCodeCombination(KeyCode.S);
        KeyCombination aKey = new KeyCodeCombination(KeyCode.A);
        KeyCombination dKey = new KeyCodeCombination(KeyCode.D);
        KeyCombination upKey = new KeyCodeCombination(KeyCode.UP);
        KeyCombination downKey = new KeyCodeCombination(KeyCode.DOWN);
        KeyCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

        scene.getAccelerators().put(wKey, () -> handleCameraForward(new ActionEvent()));
        scene.getAccelerators().put(upKey, () -> handleCameraUp(new ActionEvent()));
        scene.getAccelerators().put(sKey, () -> handleCameraBackward(new ActionEvent()));
        scene.getAccelerators().put(downKey, () -> handleCameraDown(new ActionEvent()));
        scene.getAccelerators().put(aKey, () -> handleCameraLeft(new ActionEvent()));
        scene.getAccelerators().put(dKey, () -> handleCameraRight(new ActionEvent()));
        scene.getAccelerators().put(ctrlF, () -> onOpenModelHandler(null));
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
        else sceneStorage.makeVisible(filename);
        refreshGui();

        endOperation();
    }

    @Override
    public void onChangeActiveStatusHandler(String filename) {
        startOperation();

        RenderObject renderObject = sceneStorage.getRenderObject(filename);
        if (renderObject.getStatus().equals(RenderObjectStatus.ACTIVE))
            sceneStorage.makeUnactive(filename);
        else sceneStorage.makeActive(filename);
        refreshGui();

        endOperation();
    }

    @Override
    public void refreshGui() {
        refreshModelList();
    }

    private void refreshModelList() {
        modelsVBox.getChildren().clear();
        for (RenderObject renderObject : sceneStorage.getRenderObjects()) {
            Node node = GuiElementsBuilder.createObjectNode(
                    renderObject.getMetadata(),
                    this::onDeleteModelHandler,
                    this::onAddTextureHandler,
                    this::onRemoveTextureHandler,
                    this::onChangeVisibilityHandler,
                    this::onChangeActiveStatusHandler
            );
            modelsVBox.getChildren().add(node);
        }
    }

    @FXML
    public void handleCameraMove(MouseEvent mouseEvent) {
        // todo: как то исходя из движения мыши надо билдить вектор
        transformationController.moveCameraOnVector(null);
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -Constants.TRANSLATION));
        refreshRender();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, Constants.TRANSLATION));
        refreshRender();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(Constants.TRANSLATION, 0, 0));
        refreshRender();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-Constants.TRANSLATION, 0, 0));
        refreshRender();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, Constants.TRANSLATION, 0));
        refreshRender();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -Constants.TRANSLATION, 0));
        refreshRender();
    }
}
