package carleton.sysc3303.client.connection;

import java.util.EventListener;

public interface ConnectionStatusListener extends EventListener
{
    public enum State { CONNECTED, DISCONNECTED };


    /**
     * Invoked when the connection status changes.
     *
     * @param s
     */
    public void statusChanged(State s);
}
