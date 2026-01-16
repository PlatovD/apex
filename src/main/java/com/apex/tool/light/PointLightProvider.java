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
        return -(n_x0 * barycentric[0] + n_x1 * barycentric[1] + n_x2 * barycentric[2]) * light.getX()
                - (n_y0 * barycentric[0] + n_y1 * barycentric[1] + n_y2 * barycentric[2]) * light.getY()
                - (n_z0 * barycentric[0] + n_z1 * barycentric[1] + n_z2 * barycentric[2]) * light.getZ();
    }
}
