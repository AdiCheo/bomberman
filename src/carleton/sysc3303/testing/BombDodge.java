package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Before;
import org.junit.Test;

import carleton.sysc3303.client.BotClient;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.GameBoard;
import carleton.sysc3303.server.ServerBoard;
import carleton.sysc3303.testing.server.TestGameBoard;

/**
 * test case to test player bomb dodging
 * @author Nick Mancuso
 *
 */
public class BombDodge extends BaseTest
{
    private List<String> commands;
    private ServerBoard board;
    Position target, start;

    @Before
    public void setUp()
    {
        super.setUp();
        commands = new ArrayList<String>();
        target = new Position(6, 6);
        board = new ServerBoard(20);
        start = new Position(5, 5);

        commands.add("BOMB");
        commands.add("UP");
        commands.add("RIGHT");

        board.addStartingPosition(start);
    }


    @Test(timeout = 5000)
    public void test() throws InterruptedException
    {
        server.run();
        clientConnection.run();

        TestGameBoard logic = new TestGameBoard(server, board);
        BotClient bot = new BotClient(clientConnection, moveSpeed, PlayerTypes.PLAYER);
        bot.waitForConnection();
        bot.setCommands(commands);

        int id = clientConnection.getId();
        assertEquals("There is one player.", 1, logic.getConnectedPlayers());
        assertEquals("Bot at start.", start, logic.getPlayerPosition(id));
        assertEquals("Number of explosions", 0, logic.getNumExplosions());

        logic.setGameState(State.STARTED);
        bot.waitForCompletion();

        // bomb should explode by this point
        Thread.sleep(GameBoard.BOMB_TIMEOUT);

        assertEquals("Bot is dead", false, logic.isPlayerDead(id));
        assertEquals("Bot is in the right place", target, logic.getPlayerPosition(id));
        assertEquals("Number of explosions", 1, logic.getNumExplosions());
    }

}
