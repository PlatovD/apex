package com.apex.shader;

import com.apex.math.Vector3f;
import com.apex.model.scene.Light;
import com.apex.model.texture.Texture;
import com.apex.tool.colorization.ColorData;

import java.util.List;

public class SimpleShader implements Shader {
    @Override
    public int calcPixel(ColorData colorData, Texture texture, Vector3f look, float n_x0, float n_x1, float n_x2, float n_y0, float n_y1, float n_y2, float n_z0, float n_z1, float n_z2, double[] barycentric, List<Light> lights) {
        float nx = (float) (n_x0 * barycentric[0] + n_x1 * barycentric[1] + n_x2 * barycentric[2]);
        float ny = (float) (n_y0 * barycentric[0] + n_y1 * barycentric[1] + n_y2 * barycentric[2]);
        float nz = (float) (n_z0 * barycentric[0] + n_z1 * barycentric[1] + n_z2 * barycentric[2]);

        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }
        nx = -nx;
        ny = -ny;
        nz = -nz;

        double resDot = 0;
        for (Light light : lights) {
            Vector3f lightVector =
            resDot = nx * light.getX() + ny * light.getY() + nz * light.getZ();
        }

        return Math.max(dot, 0);
    }
}
