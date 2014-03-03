package carleton.sysc3303.common;


/**
 * Like Point2D, but for integers only.
 *
 * @author Kirill Stepanov.
 */
public class Position
{
    private int x, y;


    public Position(int x, int y)
    {
        this.x = x;
        this.y = y;
    }


    public int getX()
    {
        return x;
    }


    public int getY()
    {
        return y;
    }


    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Position))
        {
            return false;
        }

        Position p = (Position)o;

        return p.x == x && p.y == y;
    }


    public String toString()
    {
        return String.format("(%d, %d)", x, y);
    }
}
