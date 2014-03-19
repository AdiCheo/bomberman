package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.*;

public class PlayerMessage implements IMessage
{
    private int pid, x, y;
    private PlayerTypes type;
    private String name;


    /**
     * Constructor.
     *
     * @param pid	The player's id.
     * @param x		X-coordinate
     * @param y		Y-coordinate
     */
    public PlayerMessage(int pid, int x, int y, PlayerTypes type, String name)
    {
        this.pid = pid;
        this.x = x;
        this.y = y;
        this.type = type;
        this.name = name;
    }


    /**
     * Alternative constructor.
     *
     * @param pid
     * @param pos
     */
    public PlayerMessage(int pid, Position pos, PlayerTypes type, String name)
    {
        this(pid, pos.getX(), pos.getY(), type, name);
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public PlayerMessage(String data)
    {
        String[] args = data.split(",");

        this.pid = Integer.parseInt(args[0]);
        this.x = Integer.parseInt(args[1]);
        this.y = Integer.parseInt(args[2]);
        this.type = PlayerTypes.valueOf(args[3]);
        this.name = args[4];
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


    /**
     * Gets the player's name.
     *
     * @return
     */
    public String getName()
    {
        return name;
    }


    /**
     * Creates an instance of the position.
     *
     * @return
     */
    public Position getPosition()
    {
        return new Position(x, y);
    }


    @Override
    public String serialize()
    {
        return String.format("%d,%d,%d,%s,%s", pid, x, y, type.toString(), name);
    }
}
