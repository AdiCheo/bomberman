package carleton.sysc3303.client.connection;

import java.util.EventListener;
import carleton.sysc3303.client.Types;

public abstract class ObjectCreatedListener implements EventListener
{
    public abstract void objectCreated(Types type, Position pos);
}
