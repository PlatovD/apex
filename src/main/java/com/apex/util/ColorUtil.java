package com.apex.util;

import javafx.scene.paint.Color;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ColorUtil {
    public static Color createColorFromBarycentric(double[] barycentric, Color color1, Color color2, Color color3) {
        double a = max(0, min(1, barycentric[0]));
        double b = max(0, min(1, barycentric[1]));
        double c = max(0, min(1, barycentric[2]));

        return new Color(
                color1.getRed() * a + color2.getRed() * b + color3.getRed() * c,
                color1.getGreen() * a + color2.getGreen() * b + color3.getGreen() * c,
                color1.getBlue() * a + color2.getBlue() * b + color3.getBlue() * c,
                1);

    }

    public static int toARGB(Color color) {
        return ((int)(color.getOpacity() * 255) << 24) |
                ((int)(color.getRed()     * 255) << 16) |
                ((int)(color.getGreen()   * 255) << 8)  |
                ((int)(color.getBlue()    * 255));
    }

    public static Color fromARGB(int argb) {
        double a = ((argb >> 24) & 0xFF) / 255.0;
        double r = ((argb >> 16) & 0xFF) / 255.0;
        double g = ((argb >> 8) & 0xFF) / 255.0;
        double b = (argb & 0xFF) / 255.0;
        return new Color(r, g, b, a);
    }
}
