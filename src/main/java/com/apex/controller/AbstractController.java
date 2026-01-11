package com.apex.controller;

import com.apex.io.textureloader.TextureLoader;
import com.apex.io.util.IOProcessor;
import com.apex.model.geometry.Model;
import com.apex.model.scene.RenderObject;
import com.apex.reflection.AutoInject;
import com.apex.render.RenderEngine;
import com.apex.storage.SceneStorage;
import com.apex.storage.transformation.TransformationController;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;

import javax.vecmath.Vector3f;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.apex.io.read.ObjReader;
import com.apex.model.scene.Camera;

import static com.apex.core.Constants.TRANSLATION;

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

    @FXML
    protected AnchorPane anchorPane;

    @FXML
    protected ImageView imageView;

    @FXML
    protected Canvas canvas;

    @AutoInject
    protected Camera camera;

    @FXML
    protected javafx.scene.control.ListView<String> objectsList;

    @FXML
    protected javafx.scene.control.TextField transX, transY, transZ;
    @FXML
    protected javafx.scene.control.TextField rotX, rotY, rotZ;
    @FXML
    protected javafx.scene.control.TextField scaleX, scaleY, scaleZ;

    @FXML
    protected AnchorPane renderPane;

    @FXML
    protected void initialize() {
        // Bind canvas and imageView to the renderPane (center) size
        canvas.widthProperty().bind(renderPane.widthProperty());
        canvas.heightProperty().bind(renderPane.heightProperty());

        imageView.fitWidthProperty().bind(renderPane.widthProperty());
        imageView.fitHeightProperty().bind(renderPane.heightProperty());

        anchorPane.setOnMouseClicked(event -> anchorPane.requestFocus());
        canvas.setOnMouseClicked(event -> anchorPane.requestFocus());

        objectsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateUIForSelectedModel(newVal);
            }
        });

        setupTransformListeners();
    }

    private void setupTransformListeners() {
        javafx.beans.value.ChangeListener<String> transformListener = (obs, oldVal, newVal) -> updateModelFromUI();

        transX.textProperty().addListener(transformListener);
        transY.textProperty().addListener(transformListener);
        transZ.textProperty().addListener(transformListener);

        rotX.textProperty().addListener(transformListener);
        rotY.textProperty().addListener(transformListener);
        rotZ.textProperty().addListener(transformListener);

        scaleX.textProperty().addListener(transformListener);
        scaleY.textProperty().addListener(transformListener);
        scaleZ.textProperty().addListener(transformListener);
    }

    private void updateUIForSelectedModel(String modelName) {
        com.apex.model.scene.RenderObject ro = sceneStorage.getRenderObject(modelName);
        if (ro == null)
            return;

        transX.setText(String.valueOf(ro.getPosition().getX()));
        transY.setText(String.valueOf(ro.getPosition().getY()));
        transZ.setText(String.valueOf(ro.getPosition().getZ()));

        rotX.setText(String.valueOf(ro.getRotation().getX()));
        rotY.setText(String.valueOf(ro.getRotation().getY()));
        rotZ.setText(String.valueOf(ro.getRotation().getZ()));

        scaleX.setText(String.valueOf(ro.getScale().getX()));
        scaleY.setText(String.valueOf(ro.getScale().getY()));
        scaleZ.setText(String.valueOf(ro.getScale().getZ()));
    }

    private void updateModelFromUI() {
        String selected = objectsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        RenderObject ro = sceneStorage.getRenderObject(selected);
        if (ro == null)
            return;

        try {
            ro.getPosition().setX(Float.parseFloat(transX.getText()));
            ro.getPosition().setY(Float.parseFloat(transY.getText()));
            ro.getPosition().setZ(Float.parseFloat(transZ.getText()));

            ro.getRotation().setX(Float.parseFloat(rotX.getText()));
            ro.getRotation().setY(Float.parseFloat(rotY.getText()));
            ro.getRotation().setZ(Float.parseFloat(rotZ.getText()));

            ro.getScale().setX(Float.parseFloat(scaleX.getText()));
            ro.getScale().setY(Float.parseFloat(scaleY.getText()));
            ro.getScale().setZ(Float.parseFloat(scaleZ.getText()));

            transformationController.updateWorldMatrixForObject(ro);
            refresh();
        } catch (NumberFormatException e) {
            // Ignore invalid input while typing
        }
    }

    @FXML
    protected void onDeleteModel() {
        String selected = objectsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        sceneStorage.deleteModel(selected);
        objectsList.getItems().remove(selected);
        refresh();
    }

    @FXML
    protected void onRemoveTexture() {
        String selected = objectsList.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        sceneStorage.deleteTexture(selected);
        refresh();
    }

    protected abstract void startOperation();

    protected abstract void endOperation();

    protected abstract void refresh();

    protected abstract String getTargetModelName();

    @FXML
    protected void onOpenTextureMenuItemClick() {
        String selected = objectsList.getSelectionModel().getSelectedItem();
        // Fallback to last loaded if nothing selected (optional, or force selection)
        // prompt implies we add texture to a model. If none selected
        // Let's use getTargetModelName() logic which returns last loaded if overriden,
        // OR prefer selected.
        String target = selected != null ? selected : getTargetModelName();

        if (target == null) {
            System.out.println("No model selected to apply texture");
            return;
        }

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
            sceneStorage.addTexture(target, file.getName(), image);
            refresh();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        endOperation();
    }

    protected String lastLoadedModelName;

    @FXML
    protected void onOpenModelMenuItemClick() {
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
            lastLoadedModelName = file.getName();
            objectsList.getItems().add(file.getName());
            objectsList.getSelectionModel().select(file.getName());
            refresh();
        } catch (IOException exception) {
            // todo: обработка ошибок
        }
        endOperation();
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
        refresh();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
        refresh();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
        refresh();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
        refresh();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
        refresh();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
        refresh();
    }
}
