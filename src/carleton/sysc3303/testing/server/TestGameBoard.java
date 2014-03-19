package carleton.sysc3303.testing.server;

import carleton.sysc3303.common.Position;
import carleton.sysc3303.common.connection.StateMessage;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.*;
import carleton.sysc3303.server.connection.IServer;

public class TestGameBoard extends GameBoard
{
    /**
     * Constructor.
     *
     * @param server
     * @param b
     */
    public TestGameBoard(IServer server, ServerBoard b)
    {
        super(server, b);
    }


    /**
     * Gets the position of player with a certain id.
     *
     * @param p
     */
    public synchronized Position getPlayerPosition(int p)
    {
        return playerPositions.get(p);
    }


    /**
     * Sets the game's state.
     *
     * @param s
     */
    public synchronized void setGameState(State s)
    {
        currentState = s;

        if(currentState == State.STARTED)
        {
            startGame();
        }
    }


    /**
     * Gets the number of connected players.
     * Includes monster and regular players.
     *
     * @return
     */
    public synchronized int getConnectedPlayers()
    {
        return players.size();
    }


    /**
     * Checks if the player with the given id is dead.
     *
     * @param id
     * @return
     */
    public synchronized boolean isPlayerDead(int id)
    {
        return players.get(id).isDead();
    }


    /**
     * Gets the total number of explosions that happened.
     *
     * @return
     */
    public synchronized int getNumExplosions()
    {
        return explosionCounter;
    }


    /**
     * Gets the current game state.
     *
     * @return
     */
    public synchronized StateMessage.State getState()
    {
        return currentState;
    }
}
