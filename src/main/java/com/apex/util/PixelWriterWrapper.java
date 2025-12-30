package com.apex.util;

import com.apex.reflection.AutoCreation;
import javafx.scene.image.PixelWriter;

@AutoCreation
public class PixelWriterWrapper {
    private PixelWriter pixelWriter;

    public PixelWriter getPixelWriter() {
        return pixelWriter;
    }

    public void setPixelWriter(PixelWriter pixelWriter) {
        this.pixelWriter = pixelWriter;
    }
}
