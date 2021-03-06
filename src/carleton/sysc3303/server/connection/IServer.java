package carleton.sysc3303.server.connection;

import carleton.sysc3303.common.connection.IMessage;

public interface IServer extends Runnable
{
    /**
     * Stops the server and drops all connected clients.
     */
    public void exit();


    /**
     * Sends a message to one specific client.
     *
     * @param	m
     * @param	c
     */
    public void queueMessage(IMessage m, IClient c);


    /**
     * Sends a message to all connected clients.
     *
     * @param m
     */
    public void queueMessage(IMessage m);


    /**
     * Adds a new listener that listens for new connections.
     *
     * @param cl
     */
    public void addConnectionListener(ConnectionListener cl);


    /**
     * Adds a new listener that listens for disconnections.
     *
     * @param cl
     */
    public void addDisconnectionListener(DisconnectionListener cl);


    /**
     * Removes a client from the current list.
     *
     * @param c
     */
    public void removeClient(IClient c);


    /**
     * Adds a new listener that listens for messages from connected clients.
     *
     * @param ml
     */
    public void addMessageListener(MessageListener ml);
    
    public void setFrequency(int f);
}
