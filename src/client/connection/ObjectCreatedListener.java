package client.connection;

import java.util.EventListener;
import client.Types;

public abstract class ObjectCreatedListener implements EventListener
{
    public abstract void objectCreated(Types type, Position pos);
}
