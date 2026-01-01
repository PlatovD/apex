package com.apex.tool.colorization;

import com.apex.model.texture.Texture;

public class DefaultColorProvider implements ColorProvider {
    @Override
    public int getColor(ColorData colorData, Texture texture) {
        double[] barycentric = colorData.barycentric;
        float u = (float) (colorData.u0 * barycentric[0] + colorData.u1 * barycentric[1] + colorData.u2 * barycentric[2]);
        float v = (float) (colorData.v0 * barycentric[0] + colorData.v1 * barycentric[1] + colorData.v2 * barycentric[2]);
        return texture.getPixelColor(u, v);
    }
}
