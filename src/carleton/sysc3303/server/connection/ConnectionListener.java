package carleton.sysc3303.server.connection;

import java.util.EventListener;

public interface ConnectionListener extends EventListener
{
    /**
     * Listens to new connections.
     *
     * @param c
     * @param connected
     * @param isSpectator
     */
    public abstract void connectionChanged(IClient c, boolean connected, String args);
}
