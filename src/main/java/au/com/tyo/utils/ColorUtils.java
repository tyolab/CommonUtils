package au.com.tyo.utils;

import java.util.List;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 29/8/17.
 */

public class ColorUtils {

    /**
     *
     * @param color
     * @return
     */
    public static int getIntFromColor(List<Float> color) {
        float[] array = new float[color.size()];
        for (int i = 0; i < array.length; ++i)
            array[i] = color.get(i);
        return getIntFromColor(array);
    }

    /**
     *
     * @param color
     * @return
     */
    public static int getIntFromColor(float[] color) {
        float r, g, b, alpha;
        r = color[0];
        g = color[1];
        b = color[2];

        if (color.length == 3)
            alpha = 1;
        else
            alpha = color[3];
        return getIntFromColor(r, g, b, alpha);
    }

    /**
     *
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public static int getIntFromColor(float red, float green, float blue) {
        return getIntFromColor(red, green, blue, 1);
    }

    /**
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     * @return
     */
    public static int getIntFromColor(float red, float green, float blue, float alpha){
        int R = Math.round(255 * red);
        int G = Math.round(255 * green);
        int B = Math.round(255 * blue);
        int alphaInt = Math.round(255 * alpha);

        alphaInt = (alphaInt << 24) & 0xFF000000;
        R = (R << 16) & 0x00FF0000;
        G = (G << 8) & 0x0000FF00;
        B = B & 0x000000FF;

        return alphaInt | R | G | B;
    }

    /**
     *
     * @param color
     * @return
     */
    public static String toHexString(int color) {
        return String.format("#%08X", (0xFFFFFFFF & color));
    }

    /**
     *
     * @param color1
     * @param color2
     * @param distance, (0 - 1)
     * @return
     */
    public static int getGradientColor(float[] color1, float[] color2, float distance) {
        float R = color1[0] + distance * (color2[0] - color1[0]);
        float G = color1[1] + distance * (color2[1] - color1[1]);
        float B = color1[2] + distance * (color2[2] - color1[2]);
        return getIntFromColor(R, G, B);
    }
}
