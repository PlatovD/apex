package com.apex.gui.controller;

import com.apex.buffer.CustomIntArrayBasedRasterizationBuffer;
import com.apex.reflection.AutoInject;
import com.apex.reflection.ReflectionScanner;
import com.apex.util.PixelWriterWrapper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class BaseGuiController extends AbstractController {
    @AutoInject
    private CustomIntArrayBasedRasterizationBuffer buffer;

    private Timeline timeline;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        PixelWriterWrapper pixelWriterWrapper = (PixelWriterWrapper) ReflectionScanner
                .findAssignableBeanByClass(PixelWriterWrapper.class);
        pixelWriterWrapper.setPixelWriter(canvas.getGraphicsContext2D().getPixelWriter());

        // Bind canvas dimensions to renderPane for proper rendering
        canvas.widthProperty().bind(renderPane.widthProperty());
        canvas.heightProperty().bind(renderPane.heightProperty());

        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            if (width <= 0 || height <= 0)
                return;

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            activeCameraWrapper.getActiveCamera().setAspectRatio((float) (width / height));

            if (sceneStorage.hasAnyModels()) {
                renderEngine.render();
            }
        });
        Platform.runLater(() -> {
            canvas.requestFocus();
        });
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    @Override
    public void startOperation() {
        timeline.stop();
    }

    @Override
    public void endOperation() {
        timeline.play();
    }

    @Override
    public void refreshBuffer(int newWidth, int newHeight) {
        buffer.updateBufferForNewScreenSizes(newWidth, newHeight);
        refreshRender();
    }

    @Override
    public void refreshRender() {
    }
}