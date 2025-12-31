package com.apex;

import com.apex.io.textureloader.TextureLoader;
import com.apex.model.geometry.Model;
import com.apex.model.scene.FrameBuffer;
import com.apex.model.scene.SceneStorage;
import com.apex.reflection.AutoInject;
import com.apex.core.Constants;
import com.apex.render_engine.RenderEngine;
import com.apex.render_engine.pipeline.Pipeline;
import com.apex.tool.normals.NormalCalculator;
import com.apex.tool.triangulator.Triangulator;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import javax.vecmath.Vector3f;

import com.apex.io.read.ObjReader;
import com.apex.model.scene.Camera;

import static com.apex.core.Constants.TRANSLATION;

public class GuiController {
    @AutoInject
    private SceneStorage sceneStorage;

    @AutoInject
    private RenderEngine renderEngine;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @AutoInject
    private Camera camera; // тоже плохо тут держать

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        renderEngine.initialize(canvas.getGraphicsContext2D().getPixelWriter());

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (sceneStorage.hasAnyModels()) {
                renderEngine.render();
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
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
        Path fileName = Path.of(file.getAbsolutePath());

        try {
            Image image = TextureLoader.loadTextureFromFile(fileName.toString());
            sceneStorage.addTexture(image, 1); // пока что так для теста
        } catch (Exception e) {

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
            sceneStorage.addModel(model);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    // Все что дальше - прямое управление камерой. Желательно это делать не так и не внедрять камеру сюда напрямую
    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }
}