package com.apex.gui.controller;

import com.apex.buffer.JavaFXBasedRasterizationBuffer;
import com.apex.core.Constants;
import com.apex.model.scene.Camera;
import com.apex.reflection.AutoInject;
import com.apex.reflection.ReflectionScanner;
import com.apex.util.ActiveCameraWrapper;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.net.URL;
import java.util.ResourceBundle;

public class JavaFXRasterizationBufferGUIController extends AbstractController {
    @AutoInject
    private JavaFXBasedRasterizationBuffer buffer;
    @AutoInject
    private ActiveCameraWrapper activeCameraWrapper;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        canvas.setVisible(false);
        imageView.setSmooth(false);
        imageView.setImage(buffer.getWritableImage());
        Platform.runLater(imageView::requestFocus);
    }

    @Override
    public void startOperation() {
    }

    @Override
    public void endOperation() {
        refreshRender();
    }

    @Override
    public void refreshRender() {
        renderEngine.render();
    }

    @Override
    public void refreshBuffer(int newWidth, int newHeight) {
        buffer.updateBufferForNewScreenSizes(newWidth, newHeight);
        activeCameraWrapper.getActiveCamera().setAspectRatio((float) Constants.SCENE_WIDTH / Constants.SCENE_HEIGHT);
        imageView.setImage(buffer.getWritableImage());
        refreshRender();
    }
}