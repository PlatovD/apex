package com.apex.io.textureloader;

import javafx.scene.image.Image;

public class TextureLoader {
    public static Image loadTextureFromFile(String path) {
        return new Image(path);
    }
}
