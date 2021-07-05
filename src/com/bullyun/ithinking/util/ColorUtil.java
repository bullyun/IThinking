package com.bullyun.ithinking.util;

import javafx.scene.paint.Color;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class ColorUtil {

    public static String toStyleColor(Color color) {
        String string = Integer.toHexString(getColorValue(color.getRed()));
        string += Integer.toHexString(getColorValue(color.getGreen()));
        string += Integer.toHexString(getColorValue(color.getBlue()));
        return "#" + string;
    }

    private static int getColorValue(double value) {
        int ivalue = (int) (value * 255.0 + 0.5);
        ivalue = max(0, min(255, ivalue));
        return ivalue;
    }
}
