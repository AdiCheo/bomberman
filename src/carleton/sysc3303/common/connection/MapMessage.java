package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.*;

public class MapMessage implements IMessage
{
    private Board b;


    /**
     * Constructor.
     *
     * @param walls
     */
    public MapMessage(Board b)
    {
        this.b = b;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public MapMessage(String data)
    {
        this.b = new Board(data);
    }


    /**
     * Gets the data.
     *
     * @return
     */
    public Board getBoard()
    {
        return b;
    }


    @Override
    public String serialize()
    {
        return b.serialize();
    }
}
