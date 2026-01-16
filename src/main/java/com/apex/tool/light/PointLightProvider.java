package com.apex.tool.light;

import com.apex.math.Vector3f;

public class PointLightProvider implements LightProvider {
    @Override
    public double calcLightFactor(
            float n_x0, float n_x1, float n_x2,
            float n_y0, float n_y1, float n_y2,
            float n_z0, float n_z1, float n_z2,
            Vector3f light,
            double[] barycentric
    ) {
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
        double dot = nx * light.getX() + ny * light.getY() + nz * light.getZ();
        return Math.max(dot, 0);
    }
}
