package carleton.sysc3303.server.connection;

import java.util.EventListener;


/**
 * Separated from Connection listener because arguments differ.
 */
public interface DisconnectionListener extends EventListener
{
    /**
     * Called when a client disconnects.
     *
     * @param c
     */
    public void disconnected(IClient c);
}
