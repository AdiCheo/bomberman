package carleton.sysc3303.common.connection;

public class MoveMessage implements IMessage
{
    private int pid, x, y;


    /**
     * Constructor.
     *
     * @param pid	The player's id.
     * @param x		X-coordinate
     * @param y		Y-coordinate
     */
    public MoveMessage(int pid, int x, int y)
    {
        this.pid = pid;
        this.x = x;
        this.y = y;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public MoveMessage(byte[] data)
    {
        String[] args = new String(data).split(",");

        this.pid = Integer.parseInt(args[0]);
        this.x = Integer.parseInt(args[1]);
        this.y = Integer.parseInt(args[2]);
    }


    /**
     * Gets the player's id.
     *
     * @return
     */
    public int getPid()
    {
        return pid;
    }


    /**
     * Gets the player's X-coordinate.
     *
     * @return
     */
    public int getX()
    {
        return x;
    }


    /**
     * Gets the player's Y-coordinate.
     *
     * @return
     */
    public int getY()
    {
        return y;
    }


    @Override
    public String serialize()
    {
        return pid + "," + x + "," + y;
    }
}
