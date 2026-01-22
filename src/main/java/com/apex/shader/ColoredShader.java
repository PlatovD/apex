package com.apex.shader;

import com.apex.core.Constants;
import com.apex.model.scene.Light;
import com.apex.tool.rasterization.DrawableModelAttribute;
import com.apex.tool.rasterization.PixelAttribute;
import com.apex.tool.rasterization.SceneAttribute;

public class ColoredShader implements Shader {
    @Override
    public int calcPixel(PixelAttribute pixelAttribute, SceneAttribute sceneAttribute, DrawableModelAttribute modelAttribute) {
        // тут начальный цвет из текстуры
        double u = pixelAttribute.u;
        double v = pixelAttribute.v;
        int color = modelAttribute.texture.getPixelColor(u, v);

        double lightR = 0;
        double lightG = 0;
        double lightB = 0;

        // коэффициент освещенности
        for (Light light : sceneAttribute.lights) {
            // создаю вектор от источника света до пикселя
            double lightDirX = pixelAttribute.worldX - light.getPosition().getX();
            double lightDirY = pixelAttribute.worldY - light.getPosition().getY();
            double lightDirZ = pixelAttribute.worldZ - light.getPosition().getZ();

            double len = Math.sqrt(lightDirX * lightDirX + lightDirY * lightDirY + lightDirZ * lightDirZ);
            if (len > Constants.EPS) {
                lightDirX /= len;
                lightDirY /= len;
                lightDirZ /= len;
            }

            double currentFactor = -lightDirX * pixelAttribute.normalX + -lightDirY * pixelAttribute.normalY + -lightDirZ * pixelAttribute.normalZ;
            if (currentFactor > 0) {
                lightR += light.getColorR() / 255d * currentFactor;
                lightG += light.getColorG() / 255d * currentFactor;
                lightB += light.getColorB() / 255d * currentFactor;
            }
        }
        return applyFactorToColor(color, lightR, lightG, lightB);
    }

    private int applyFactorToColor(int color, double rl, double gl, double bl) {
        double tr = ((color >> 16) & 0xFF) / 255.0;
        double tg = ((color >> 8) & 0xFF) / 255.0;
        double tb = (color & 0xFF) / 255.0;

        double r = tr * rl;
        double g = tg * gl;
        double b = tb * bl;

        r *= 1d / (1 + r);
        g *= 1d / (1 + g);
        b *= 1d / (1 + b);

        int finalR = (int) Math.min(255, r * 255);
        int finalG = (int) Math.min(255, g * 255);
        int finalB = (int) Math.min(255, b * 255);

        return 0xFF << 24 | (finalR & 0xFF) << 16 | (finalG & 0xFF) << 8 | (finalB & 0xFF);
    }

}
