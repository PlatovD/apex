package com.apex.tool.colorization;

import com.apex.core.Constants;

public class ColorData {
    public double[] barycentric;
    public double uOverW0, vOverW0, uOverW1, vOverW1, uOverW2, vOverW2;
    public double invW0, invW1, invW2;
    public double lightFactor = 1;
    public double MIN_LIGHT_FACTOR = Constants.MIN_LIGHT_FACTOR;
    public boolean textured = true;
}
