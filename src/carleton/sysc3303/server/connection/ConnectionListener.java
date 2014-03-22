package carleton.sysc3303.server.connection;

import java.util.EventListener;

public interface ConnectionListener extends EventListener
{
    /**
     * Listens to new connections.
     *
     * @param c
     * @param args
     */
    public abstract void connected(IClient c, String args);
}
