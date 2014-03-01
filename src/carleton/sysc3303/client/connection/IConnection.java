package carleton.sysc3303.client.connection;

import java.awt.event.ActionListener;

/**
 * Interface that represents a connection to the server.
 *
 * @author Kirill Stepanov.
 */
public interface IConnection
{
    /**
     * Adds a listener for character/enemy movements.
     *
     * @param e
     */
    public void addMoveListener(MoveListener e);

    /**
     * Adds a listener that listens for newly created characters/enemies.
     *
     * @param e
     */
    public void addObjectCreatedListener(ObjectCreatedListener e);


    /**
     * Adds a listener that listens for map changes.
     *
     * @param e
     */
    public void addMapListener(MapListener e);


    /**
     * Adds a listener that listens for connectivity.
     * This and the following method should be changed
     * to a single one that emits an ENUM, probably.
     *
     * @param e
     */
    public void addConnectedListener(ActionListener e);


    /**
     * Adds a listener that listens for lack of connectivity.
     *
     * @param e
     */
    public void addDisconnectedListener(ActionListener e);
}
