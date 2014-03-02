package carleton.sysc3303.server.connection;

import carleton.sysc3303.common.connection.IMessage;

public interface IServer extends Runnable
{
    /**
     * Stops the server and drops all connected clients.
     */
    public void exit();


    /**
     * Gets list of all currently connected clients.
     *
     * @return
     */
    public IClient[] getClients();


    /**
     * Sends a message to one specific client.
     *
     * @param	m
     * @param	c
     */
    public void pushMessage(IMessage m, IClient c);


    /**
     * Sends a message to all connected clients.
     *
     * @param m
     */
    public void pushMessageAll(IMessage m);


    /**
     * Adds a new listener that listens for new connections.
     *
     * @param cl
     */
    public void addConnectionListener(ConnectionListener cl);


    /**
     * Adds a new listener that listens for messages from connected clients.
     *
     * @param ml
     */
    public void addMessageListener(MessageListener ml);
}
