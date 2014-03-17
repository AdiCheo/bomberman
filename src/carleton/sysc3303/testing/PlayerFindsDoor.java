package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import carleton.sysc3303.client.BotClient;
import carleton.sysc3303.common.PlayerTypes;
import carleton.sysc3303.common.Position;
import carleton.sysc3303.common.Tile;
import carleton.sysc3303.common.connection.StateMessage;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.ServerBoard;
import carleton.sysc3303.testing.server.TestGameBoard;

public class PlayerFindsDoor extends BaseTest {
    private List<String> commands;
    private ServerBoard board;
    
    @Before
    public void setUp()
    {
        super.setUp();
        commands = new ArrayList<String>();
        board = new ServerBoard(20);
        board.setTile(6,7,Tile.EXIT);
        		
        commands.add("UP");
        commands.add("DOWN");
        commands.add("UP");

        board.addStartingPosition(new Position(6, 6));
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

        assertEquals("Check starting position of bot", new Position(6, 6), logic.getPlayerPosition(id));
        
        // starting the game causes the bots to start processing commands
        logic.setGameState(State.STARTED);

        bot.waitForCompletion();

        //Verify bot reaches exit and the game has ended
        assertEquals("Check if game has ended", StateMessage.State.END, logic.getState());
    }
}

