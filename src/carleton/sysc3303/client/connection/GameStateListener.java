package carleton.sysc3303.client.connection;

import carleton.sysc3303.common.connection.StateMessage;

public abstract class GameStateListener
{
    /**
     * Method to be called when the state changes.
     *
     * @param state
     */
    public abstract void stateChanged(StateMessage.State state);
}
