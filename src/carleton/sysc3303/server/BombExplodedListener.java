package carleton.sysc3303.server;

import java.util.EventListener;

public abstract class BombExplodedListener implements EventListener
{
    /**
     * Called when a bomb explodes.
     */
    public abstract void bombExploded(int owner, int bomb);
}
