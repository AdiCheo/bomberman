package carleton.sysc3303.client.gui;

import java.awt.Color;
import java.awt.image.RGBImageFilter;


/**
 * A filter that converts a specific color to transparency.
 */
public class TransparencyFilter extends RGBImageFilter
{
    private int color;


    /**
     * Constructor.
     *
     * @param c
     */
    public TransparencyFilter(Color c)
    {
        color = c.getRGB() | 0xFF000000;
    }


    @Override
    public final int filterRGB(int x, int y, int rgb)
    {
        if((rgb | 0xFF000000) == color)
        {
            return 0x00FFFFFF & rgb;
        }

        return rgb;
    }
}
