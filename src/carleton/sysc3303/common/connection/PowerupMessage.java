package carleton.sysc3303.common.connection;

import carleton.sysc3303.common.Position;

public class PowerupMessage implements IMessage
{
    public enum Action { ADD, REMOVE };
    private Action action;
    private Position p;


    /**
     * Constructor.
     *
     * @param action
     * @param p
     */
    public PowerupMessage(Action action, Position p)
    {
        this.action = action;
        this.p = p;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public PowerupMessage(String data)
    {
        String[] split = data.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        this.p = new Position(x, y);
        this.action = Action.valueOf(split[2]);
    }


    @Override
    public String serialize()
    {
        return String.format("%d,%d,%s", p.getX(), p.getY(), action.toString());
    }


    /**
     * Gets the powerup's position.
     *
     * @return
     */
    public Position getPosition()
    {
        return p;
    }


    /**
     * Gets the desired action.
     *
     * @return
     */
    public Action getAction()
    {
        return action;
    }
}
