package carleton.sysc3303.client.connection;

import java.util.EventListener;
import carleton.sysc3303.common.Tile;

public abstract class MapListener implements EventListener
{
    public abstract void newMap(Tile[][] walls);
}
