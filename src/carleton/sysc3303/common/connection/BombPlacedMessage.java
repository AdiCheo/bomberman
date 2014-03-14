package carleton.sysc3303.common.connection;

public class BombPlacedMessage implements IMessage
{
    /**
     * Default constructor.
     */
    public BombPlacedMessage()
    {
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public BombPlacedMessage(String data)
    {
    }


    @Override
    public String serialize()
    {
        return "BOMB";
    }
}
