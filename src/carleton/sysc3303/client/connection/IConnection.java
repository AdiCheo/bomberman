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
     * Adds a listener for character/enemy positions.
     *
     * @param e
     */
    public void addPositionListener(PositionListener e);


    /**
     * Adds a listener that listens for map changes.
     *
     * @param e
     */
    public void addMapListener(MapListener e);


    /**
     * Adds a listener that listens for connectivity.
     *
     * @param e
     */
    public void addConnectionStatusListener(ConnectionStatusListener e);


    /**
     * Adds a listener that listens for changes in the game state.
     *
     * @param e
     */
    public void addGameStateListener(GameStateListener e);


    /**
     * Sends a message to the server.
     *
     * @param m
     */
    public void queueMessage(IMessage m);
}
