package carleton.sysc3303.client.connection;

import java.util.EventListener;

public abstract class PositionListener implements EventListener
{
    /**
     * Invoked when something moves.
     *
     * @param object
     * @param pos
     */
    public abstract void move(int object, Position pos);
}
