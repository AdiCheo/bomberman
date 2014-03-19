package carleton.sysc3303.client.connection;

import java.util.EventListener;

import carleton.sysc3303.common.connection.StateMessage;

public interface GameStateListener extends EventListener
{
    /**
     * Method to be called when the state changes.
     *
     * @param state
     */
    public abstract void stateChanged(StateMessage.State state);
}
