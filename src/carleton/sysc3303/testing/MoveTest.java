package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import carleton.sysc3303.client.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.*;
import carleton.sysc3303.testing.server.TestGameBoard;

@RunWith(JUnit4.class)
public class MoveTest extends BaseTest
{
    private List<String> commands;
    private ServerBoard board;
    private Position target;

    @Before
    public void setUp()
    {
        super.setUp();
        commands = new ArrayList<String>();
        board = new ServerBoard(20);
        target = new Position(6, 5);

        commands.add("RIGHT");

        board.addStartingPosition(new Position(5, 5));
    }


    @Test(timeout = 500)
    public void test() throws InterruptedException
    {
        server.run();
        clientConnection.run();

        TestGameBoard logic = new TestGameBoard(server, board);
        BotClient bot = new BotClient(clientConnection, moveSpeed, PlayerTypes.PLAYER);

        bot.setCommands(commands);
        bot.waitForConnection();
        logic.setGameState(State.STARTED);
        bot.waitForCompletion();

        int id = clientConnection.getId();
        assertEquals(id, 0); // verify the player got the correct id

        Position pos = logic.getPlayerPosition(id);
        assertEquals(pos, target);
    }
}
