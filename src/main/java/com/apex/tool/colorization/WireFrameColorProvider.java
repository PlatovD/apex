package com.apex.tool.colorization;

import com.apex.core.Constants;
import com.apex.model.texture.Texture;

public class WireFrameColorProvider extends DefaultColorProvider {
    public WireFrameColorProvider() {
        super(null);
    }

    @Override
    public int getColor(ColorData colorData, Texture texture) {
        double[] barycentric = colorData.barycentric;
        if (isOnBorderByBarycentric(barycentric)) return Constants.WIREFRAME_COLOR;
        return super.getColor(colorData, texture);
    }

    private boolean isOnBorderByBarycentric(double[] barycentric) {
        return
                (barycentric[0] < Constants.EPS + 0.01)
                        || (barycentric[1] < Constants.EPS + 0.01)
                        || (barycentric[2] < Constants.EPS + 0.01);
    }
}
