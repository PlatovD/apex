package com.apex.tool.colorization;

import com.apex.model.texture.Texture;

public class DefaultColorProvider implements ColorProvider {
    @Override
    public int getColor(ColorData colorData, Texture texture) {
        int color;
        if (colorData.textured && texture != null) {
            double[] barycentric = colorData.barycentric;
            double u = colorData.u0 * barycentric[0] + colorData.u1 * barycentric[1] + colorData.u2 * barycentric[2];
            double v = colorData.v0 * barycentric[0] + colorData.v1 * barycentric[1] + colorData.v2 * barycentric[2];
            color = texture.getPixelColor(u, v);
        } else {
            color = com.apex.core.Constants.color;
        }

        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        red = (int) (red * colorData.lightFactor);
        green = (int) (green * colorData.lightFactor);
        blue = (int) (blue * colorData.lightFactor);

        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        return (255 << 24) | (red << 16) | (green << 8) | blue;
    }
}
