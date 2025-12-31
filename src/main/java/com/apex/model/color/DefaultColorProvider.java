package com.apex.model.color;

import com.apex.model.texture.Texture;

public class DefaultColorProvider implements ColorProvider {
    @Override
    public int getColor(ColorData colorData, Texture texture) {
        double[] barycentric = colorData.barycentric;
        int u = 0;
        return 0;
    }
}
