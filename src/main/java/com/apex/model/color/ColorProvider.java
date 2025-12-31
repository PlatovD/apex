package com.apex.model.color;

import com.apex.model.texture.Texture;

public interface ColorProvider {
    int getColor(ColorData colorData, Texture texture);
}
