package carleton.sysc3303.common;

import carleton.sysc3303.server.Player;
/**
 * Like Point2D, but for integers only.
 *
 * @author Kirill Stepanov.
 */
public class Position
{
    private int x, y;


    /**
     * Constructor.
     *
     * @param x
     * @param y
     */
    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }


    /**
     * Gets the x-coordinate.
     *
     * @return
     */
    public int getX()
    {
        return x;
    }


    /**
     * Gets the y-coordinate.
     *
     * @return
     */
    public int getY()
    {
        return y;
    }


    /**
     * Checks if this object is equal to another.
     *
     * @param o
     * @return
     */
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Position))
        {
            return false;
        }

        Position p = (Position)o;

        return p.x == x && p.y == y;
    }


    /**
     * Display the position as a string.
     *
     * @return
     */
    public String toString()
    {
        return String.format("(%d, %d)", x, y);
    }
}
