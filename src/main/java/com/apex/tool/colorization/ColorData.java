package com.apex.tool.colorization;

import com.apex.core.Constants;

public class ColorData {
    public double[] barycentric;
    public float u0, v0, u1, v1, u2, v2;
    public double lightFactor = 1;
    public double MIN_LIGHT_FACTOR = Constants.MIN_LIGHT_FACTOR;
    public boolean textured = true;
}
