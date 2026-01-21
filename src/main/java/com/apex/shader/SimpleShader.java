package com.apex.shader;

import com.apex.tool.rasterization.DrawableModelAttribute;
import com.apex.tool.rasterization.PixelAttribute;
import com.apex.tool.rasterization.SceneAttribute;

public class SimpleShader implements Shader {
    @Override
    public int calcPixel(PixelAttribute pixelAttribute, SceneAttribute sceneAttribute, DrawableModelAttribute modelAttribute) {
        return 0;
    }
}
