package carleton.sysc3303.client.connection;

import carleton.sysc3303.common.connection.IMessage;

/**
 * Interface that represents a connection to the server.
 *
 * @author Kirill Stepanov.
 */
public interface IConnection extends Runnable
{
    /**
     * A generic message handler.
     *
     * @param e
     */
    public void addMessageListener(MessageListener e);


    /**
     * Adds a listener that listens for connectivity.
     *
     * @param e
     */
    public void addConnectionStatusListener(ConnectionStatusListener e);


    /**
     * Adds a listener that listens for messages directly to the user.
     *
     * @param e
     */
    public void addUserMessageListener(UserMessageListener e);


    /**
     * Adds a listener that listens for changes in the game state.
     *
     * @param e
     */
    public void addGameStateListener(GameStateListener e);


    /**
     * Adds a listener that listens for player's assigned id.
     *
     * @param e
     */
    public void addIdListener(IdListener e);


    /**
     * Sends a message to the server.
     *
     * @param m
     */
    public void queueMessage(IMessage m);


    /**
     * Tells the connection to do whatever it has to
     * and then exit. The call should block until it's done.
     */
    public void exit();
}
