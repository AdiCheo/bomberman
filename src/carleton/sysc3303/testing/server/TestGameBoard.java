package carleton.sysc3303.testing.server;

import carleton.sysc3303.common.Position;
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
        return player_positions.get(p);
    }


    /**
     * Sets the game's state.
     *
     * @param s
     */
    public synchronized void setGameState(State s)
    {
        current_state = s;

        if(current_state == State.STARTED)
        {
            startGame();
        }
    }
}
