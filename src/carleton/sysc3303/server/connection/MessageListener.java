package carleton.sysc3303.server.connection;

import java.util.EventListener;
import carleton.sysc3303.common.connection.IMessage;

public interface MessageListener extends EventListener
{
    /**
     * Adds a listener that listens for messages from existing clients.
     *
     * @param c
     * @param m
     */
    public abstract void newMessage(IClient c, IMessage m);
}
