package com.apex.shader;

import com.apex.tool.rasterization.DrawableModelAttribute;
import com.apex.tool.rasterization.PixelAttribute;
import com.apex.tool.rasterization.SceneAttribute;

public class NoLightingShader implements Shader {
    @Override
    public int calcPixel(PixelAttribute pixelAttribute, SceneAttribute sceneAttribute, DrawableModelAttribute modelAttribute) {
        double u = pixelAttribute.u;
        double v = pixelAttribute.v;
        return modelAttribute.texture.getPixelColor(u, v);
    }
}
