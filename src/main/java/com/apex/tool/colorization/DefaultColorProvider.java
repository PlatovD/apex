package com.apex.tool.colorization;

import com.apex.model.texture.Texture;

public class DefaultColorProvider implements ColorProvider {
    @Override
    public int getColor(ColorData colorData, Texture texture) {
        int color;
        if (colorData.textured && texture != null) {
            double[] barycentric = colorData.barycentric;
            double invW_pixel = colorData.invW0 * barycentric[0] + colorData.invW1 * barycentric[1] + colorData.invW2 * barycentric[2];
            double uOverW_pixel = colorData.uOverW0 * barycentric[0] + colorData.uOverW1 * barycentric[1] + colorData.uOverW2 * barycentric[2];
            double vOverW_pixel = colorData.vOverW0 * barycentric[0] + colorData.vOverW1 * barycentric[1] + colorData.vOverW2 * barycentric[2];

            double u_correct = uOverW_pixel / invW_pixel;
            double v_correct = vOverW_pixel / invW_pixel;
            color = texture.getPixelColor(u_correct, v_correct);
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
