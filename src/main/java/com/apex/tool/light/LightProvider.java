package com.apex.tool.light;

import com.apex.math.Vector3f;

public interface LightProvider {
    double calcLightFactor(float n_x0, float n_x1, float n_x2,
                           float n_y0, float n_y1, float n_y2,
                           float n_z0, float n_z1, float n_z2,
                           Vector3f light,
                           double[] barycentric);
}
