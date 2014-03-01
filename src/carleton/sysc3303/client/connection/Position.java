package carleton.sysc3303.client.connection;


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
}
