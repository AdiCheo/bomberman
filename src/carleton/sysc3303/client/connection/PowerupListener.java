package carleton.sysc3303.client.connection;

import java.util.EventListener;
import carleton.sysc3303.common.Position;
import carleton.sysc3303.common.connection.PowerupMessage.Action;

public abstract class PowerupListener implements EventListener
{
    public abstract void powerup(Action a, Position p);
}
