package com.apex.io.textureloader;

import javafx.scene.image.Image;

import java.io.File;

public class TextureLoader {
    public static Image loadTextureFromFile(File file) {
        return new Image(file.toURI().toString());
    }
}
