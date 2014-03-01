package carleton.sysc3303.server.connection;

import java.util.EventListener;
import carleton.sysc3303.common.connection.IMessage;

public abstract class MessageListener implements EventListener
{
    /**
     * Adds a listener that listens for messages from existing clients.
     *
     * @param c
     * @param m
     */
    public abstract void newMessage(IClient c, IMessage m);
}
