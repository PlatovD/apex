package com.apex.shader;

import com.apex.core.Constants;
import com.apex.model.scene.Light;
import com.apex.tool.rasterization.DrawableModelAttribute;
import com.apex.tool.rasterization.PixelAttribute;
import com.apex.tool.rasterization.SceneAttribute;

public class SimpleShader implements Shader {
    @Override
    public int calcPixel(PixelAttribute pixelAttribute, SceneAttribute sceneAttribute, DrawableModelAttribute modelAttribute) {
        // тут начальный цвет из текстуры
        double u = pixelAttribute.u;
        double v = pixelAttribute.v;
        int color = modelAttribute.texture.getPixelColor(u, v);

        // коэффициент освещенности
        double factor = 0;
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
            if (currentFactor > 0)
                factor += currentFactor;
        }

        factor = Math.min(1, Math.max(Constants.MIN_LIGHT_FACTOR, factor));
        return applyFactorToColor(color, factor);
    }

    private int applyFactorToColor(int color, double factor) {
        double r = color >> 16 & 0xFF;
        double g = color >> 8 & 0xFF;
        double b = color & 0xFF;
        r *= factor;
        g *= factor;
        b *= factor;
        int finalR = (int) Math.max(0, Math.min(255, r));
        int finalG = (int) Math.max(0, Math.min(255, g));
        int finalB = (int) Math.max(0, Math.min(255, b));
        return 0xFF << 24 | finalR << 16 | finalG << 8 | finalB;
    }
}
