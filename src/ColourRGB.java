/**
 * Created by Shane on 13/03/2017.
 * ID: 14870204
 * The ColourRGB class allows for Colours to be defined and referenced and promotes avoiding magic numbers.
 */
class ColourRGB {
    final float RED, GREEN, BLUE, ALPHA;

    ColourRGB(float r, float g, float b, float a) {
        RED = r;
        GREEN = g;
        BLUE = b;
        ALPHA = a;
    }
}
