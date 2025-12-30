package com.apex;

import com.apex.model.FrameBuffer;
import com.apex.reflection.AutoInject;
import com.apex.core.Constants;
import com.apex.render_engine.pipeline.Pipeline;
import com.apex.tool.normals.NormalCalculator;
import com.apex.tool.triangulator.Triangulator;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.function.Supplier;
import javax.vecmath.Vector3f;

import com.apex.model.Model;
import com.apex.io.objreader.ObjReader;
import com.apex.model.Camera;

public class GuiController {
    @AutoInject
    private Pipeline pipeline;

    @AutoInject
    private FrameBuffer frameBuffer;

    @AutoInject(name = "SimpleTriangulator")
    private Triangulator triangulator;

    final private float TRANSLATION = 0.5F;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    @AutoInject
    private Camera camera;

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(60), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                pipeline.applyAll(mesh);
                rasterize();
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    private void rasterize() {
        int[] rawPixels = frameBuffer.getRawData();
        int width = Constants.SCENE_WIDTH;
        int height = Constants.SCENE_HEIGHT;


        canvas.getGraphicsContext2D().getPixelWriter().setPixels(
                0, 0,
                width, height,
                PixelFormat.getIntArgbInstance(),
                rawPixels,
                0,
                rawPixels.length / height
        );
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
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
        triangulator.triangulateModel(mesh);
        NormalCalculator.calculateVerticesNormals(mesh);
    }

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