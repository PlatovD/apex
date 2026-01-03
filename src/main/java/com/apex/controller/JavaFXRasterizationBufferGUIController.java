package com.apex.controller;

import com.apex.buffer.JavaFXBasedRasterizationBuffer;
import com.apex.core.Constants;
import com.apex.io.textureloader.TextureLoader;
import com.apex.io.util.IOProcessor;
import com.apex.model.geometry.Model;
import com.apex.model.scene.SceneStorage;
import com.apex.reflection.AutoInject;
import com.apex.reflection.ReflectionScanner;
import com.apex.render_engine.RenderEngine;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import javax.vecmath.Vector3f;

import com.apex.io.read.ObjReader;
import com.apex.model.scene.Camera;

import static com.apex.core.Constants.TRANSLATION;

public class JavaFXRasterizationBufferGUIController implements Controller {
    @AutoInject(name = "ReadIOProcessor")
    private IOProcessor inputProcessor;
    @AutoInject(name = "WriteIOProcessor")
    private IOProcessor writeProcessor;

    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private RenderEngine renderEngine;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private ImageView imageView;

    @FXML
    private Canvas canvas;

    @AutoInject
    private Camera camera; // тоже плохо тут держать

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        canvas.setVisible(false);
        imageView.setFitWidth(Constants.SCENE_WIDTH);
        imageView.setFitHeight(Constants.SCENE_HEIGHT);
        imageView.setSmooth(true);

        JavaFXBasedRasterizationBuffer javaFXBasedRasterizationBuffer = (JavaFXBasedRasterizationBuffer) ReflectionScanner.findAssignableBeanByClass(JavaFXBasedRasterizationBuffer.class);
        imageView.setImage(javaFXBasedRasterizationBuffer.getWritableImage());
    }

    @FXML
    private void onOpenTextureMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp")
        );
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) return;

        try {
            Image image = TextureLoader.loadTextureFromFile(file);
            sceneStorage.addTexture(null, file.getName(), image); // пока что так для теста. Вместо null должно быть име модели для которой добавляют текстуру
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        redraw();
    }

    private void redraw() {
        if (sceneStorage.hasAnyModels()) {
            renderEngine.render();
        }
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            Model model = ObjReader.read(fileContent);
            inputProcessor.process(model);
            sceneStorage.addModel(file.getName(), model);
            // todo: обработка ошибок
        } catch (IOException exception) {
        }
        redraw();
    }

    // Все что дальше - прямое управление камерой. Желательно это делать не так и не внедрять камеру сюда напрямую
    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
        redraw();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
        redraw();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
        redraw();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
        redraw();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
        redraw();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
        redraw();
    }
}