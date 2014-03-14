package carleton.sysc3303.client.connection;

import java.util.EventListener;
import carleton.sysc3303.common.*;

public abstract class BombListener implements EventListener
{
    /**
     * Invoked when something moves.
     *
     * @param pos
     * @param size
     */
    public abstract void bomb(Position pos, int size);
}
