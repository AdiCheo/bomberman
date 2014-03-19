package carleton.sysc3303.client.connection;

import java.util.EventListener;

import carleton.sysc3303.common.connection.IMessage;

public interface MessageListener extends EventListener
{
    /**
     * Receives a message from the server.
     *
     * @param m
     */
    public void newMessage(IMessage m);
}
