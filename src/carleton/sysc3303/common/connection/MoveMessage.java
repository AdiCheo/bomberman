package carleton.sysc3303.common.connection;

public class MoveMessage implements IMessage
{
    public enum Direction { UP, RIGHT, DOWN, LEFT };

    private Direction dir;


    /**
     * Constructor.
     *
     * @param dir
     */
    public MoveMessage(Direction dir)
    {
        this.dir = dir;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public MoveMessage(String data)
    {
        this.dir = Direction.valueOf(data);
    }


    @Override
    public String serialize()
    {
        return dir.toString();
    }
}
