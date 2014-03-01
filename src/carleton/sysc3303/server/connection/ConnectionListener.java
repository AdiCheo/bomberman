package carleton.sysc3303.server.connection;

import java.util.EventListener;

public abstract class ConnectionListener implements EventListener
{
    /**
     * Listens to new connections.
     *
     * @param c
     */
    public abstract void newConnection(IClient c);
}
