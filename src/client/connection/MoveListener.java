package client.connection;

import java.util.EventListener;

public abstract class MoveListener implements EventListener
{
    /**
     * Invoked when something moves.
     *
     * @param object
     * @param old
     * @param new_
     */
    public abstract void move(int object, Position old, Position new_);
}
