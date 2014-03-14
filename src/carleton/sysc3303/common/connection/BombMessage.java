package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.*;

public class BombMessage implements IMessage
{
    private Position pos;
    private int size;


    /**
     * Constructor.
     */
    public BombMessage(Position pos, int size)
    {
        this.pos = pos;
        this.size = size;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public BombMessage(String data)
    {
        String[] parts = data.split(",");
        pos = new Position(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        size = Integer.parseInt(parts[2]);
    }


    @Override
    public String serialize()
    {
        return String.format("%d,%d,%d", pos.getX(), pos.getY(), size);
    }


    /**
     * Gets the bomb's position.
     *
     * @return
     */
    public Position getPosition()
    {
        return pos;
    }


    /**
     * Gets the bomb's size.
     *
     * @return
     */
    public int getSize()
    {
        return size;
    }
}
