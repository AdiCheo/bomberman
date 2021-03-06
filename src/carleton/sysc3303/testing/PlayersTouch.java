package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import carleton.sysc3303.client.BotClient;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.ServerBoard;
import carleton.sysc3303.testing.client.TestConnection;
import carleton.sysc3303.testing.server.TestGameBoard;

public class PlayersTouch extends BaseTest {
    private List<String> commands1, commands2;
    private ServerBoard board;
    private Position target1, target2;
    TestConnection clientConnection2;

    @Before
    public void setUp()
    {
        super.setUp();
        clientConnection2 = new TestConnection(server);
        commands1 = new ArrayList<String>();
        commands2 = new ArrayList<String>();
        board = new ServerBoard(20);
        target1 = new Position(6, 6);
        target2 = new Position(6, 5);

        commands1.add("DOWN");
        commands2.add("UP");

        board.addStartingPosition(new Position(6, 6));
        board.addStartingPosition(new Position(6, 5));
    }


    @Test(timeout = 1000)
    public void test() throws InterruptedException
    {
        server.run();
        clientConnection.run();
        clientConnection2.run();

        TestGameBoard logic = new TestGameBoard(server, board);

        BotClient bot1 = new BotClient(clientConnection, moveSpeed, PlayerTypes.PLAYER);
        bot1.waitForConnection();
        bot1.setCommands(commands1);

        BotClient bot2 = new BotClient(clientConnection2, moveSpeed, PlayerTypes.PLAYER);
        bot2.waitForConnection();
        bot2.setCommands(commands2);

        int bot1id = clientConnection.getId();
        int bot2id = clientConnection2.getId();

        assertEquals("There are two players connected.", 2, logic.getConnectedPlayers());

        assertEquals("Check starting position of bot 1", target1, logic.getPlayerPosition(bot1id));
        assertEquals("Check starting position of bot 2", target2, logic.getPlayerPosition(bot2id));

        // starting the game causes the bots to start processing commands
        logic.setGameState(State.STARTED);

        bot1.waitForCompletion();
        bot2.waitForCompletion();

        //Verify Collision Was Ignored
        assertEquals("Check final position of bot 1", target1, logic.getPlayerPosition(bot1id));
        assertEquals("Check final position of bot 2", target2, logic.getPlayerPosition(bot2id));
    }
}
