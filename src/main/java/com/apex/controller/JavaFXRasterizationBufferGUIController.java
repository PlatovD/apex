package com.apex.controller;

import com.apex.buffer.JavaFXBasedRasterizationBuffer;
import com.apex.core.Constants;
import com.apex.reflection.ReflectionScanner;
import javafx.fxml.FXML;

public class JavaFXRasterizationBufferGUIController extends AbstractController {

    @FXML
    protected void initialize() {
        super.initialize();
        canvas.setVisible(false);
        imageView.setSmooth(true);

        JavaFXBasedRasterizationBuffer javaFXBasedRasterizationBuffer = (JavaFXBasedRasterizationBuffer) ReflectionScanner
                .findAssignableBeanByClass(JavaFXBasedRasterizationBuffer.class);
        imageView.setImage(javaFXBasedRasterizationBuffer.getWritableImage());
    }

    @Override
    protected void startOperation() {
        // Nothing to do
    }

    @Override
    protected void endOperation() {
        refresh();
    }

    @Override
    protected void refresh() {
        renderEngine.render();
    }

    @Override
    protected String getTargetModelName() {
        return lastLoadedModelName != null ? lastLoadedModelName : "AlexNeutralWrapped.obj";
    }
}