package com.apex.tool.colorization;

import com.apex.core.Constants;

public class ColorData {
    public float[] barycentric;
    public float u0, v0, u1, v1, u2, v2;
    public float lightFactor = 1;
    public float MIN_LIGHT_FACTOR = Constants.MIN_LIGHT_FACTOR;
}
