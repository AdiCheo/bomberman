package client.connection;

import java.util.EventListener;
import java.util.List;

public abstract class MapListener implements EventListener
{
    public abstract void newMap(List<Position> blocks);
}
