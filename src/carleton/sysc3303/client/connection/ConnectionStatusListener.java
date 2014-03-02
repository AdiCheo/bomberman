package carleton.sysc3303.client.connection;

import java.util.EventListener;

public abstract class ConnectionStatusListener implements EventListener
{
    public enum State { CONNECTED, DISCONNECTED };


    /**
     * Invoked when the connection status changes.
     *
     * @param s
     */
    public abstract void statusChanged(State s);
}
