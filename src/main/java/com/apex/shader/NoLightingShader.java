package com.apex.shader;

import com.apex.math.Vector3f;
import com.apex.model.scene.Light;
import com.apex.model.texture.Texture;
import com.apex.tool.colorization.ColorData;

import java.util.List;

public class NoLightingShader implements Shader {
    @Override
    public int calcPixel(ColorData colorData, Texture texture, Vector3f look, float n_x0, float n_x1, float n_x2, float n_y0, float n_y1, float n_y2, float n_z0, float n_z1, float n_z2, double[] barycentric, List<Light> lights) {
        return 0;
    }
}
