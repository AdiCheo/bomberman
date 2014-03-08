package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.*;

public class PosMessage implements IMessage
{
    private int pid, x, y;
    private PlayerTypes type;


    /**
     * Constructor.
     *
     * @param pid	The player's id.
     * @param x		X-coordinate
     * @param y		Y-coordinate
     */
    public PosMessage(int pid, int x, int y, PlayerTypes type)
    {
        this.pid = pid;
        this.x = x;
        this.y = y;
        this.type = type;
    }


    /**
     * Alternative constructor.
     *
     * @param pid
     * @param pos
     */
    public PosMessage(int pid, Position pos, PlayerTypes type)
    {
        this(pid, pos.getX(), pos.getY(), type);
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public PosMessage(String data)
    {
        String[] args = data.split(",");

        this.pid = Integer.parseInt(args[0]);
        this.x = Integer.parseInt(args[1]);
        this.y = Integer.parseInt(args[2]);
        this.type = PlayerTypes.valueOf(args[3]);
    }


    /**
     * Gets the player type.
     *
     * @return
     */
    public PlayerTypes getType()
    {
        return type;
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
        return String.format("%d,%d,%d,%s", pid, x, y, type.toString());
    }
}
