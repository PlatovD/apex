package com.apex.controller;

import com.apex.reflection.ReflectionScanner;
import com.apex.util.PixelWriterWrapper;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.util.Duration;

public class BaseGuiController extends AbstractController {

    private Timeline timeline;

    @FXML
    protected void initialize() {
        super.initialize();
        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        PixelWriterWrapper pixelWriterWrapper = (PixelWriterWrapper) ReflectionScanner
                .findAssignableBeanByClass(PixelWriterWrapper.class);
        pixelWriterWrapper.setPixelWriter(canvas.getGraphicsContext2D().getPixelWriter());
        imageView.setVisible(false);

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

    @Override
    protected void startOperation() {
        timeline.stop();
    }

    @Override
    protected void endOperation() {
        timeline.play();
    }

    @Override
    protected void refresh() {
        // Handled by timeline, nothing to do here
    }

    @Override
    protected String getTargetModelName() {
        return lastLoadedModelName;
    }
}