package carleton.sysc3303.client.connection;

import java.util.EventListener;

public abstract class MapListener implements EventListener
{
    public abstract void newMap(boolean[][] walls);
}
