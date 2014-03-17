package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import carleton.sysc3303.client.BotClient;
import carleton.sysc3303.common.PlayerTypes;
import carleton.sysc3303.common.Position;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.ServerBoard;
import carleton.sysc3303.testing.client.TestConnection;
import carleton.sysc3303.testing.server.TestGameBoard;

/**
 * test case to test dead player's messages
 * @author Nick Mancuso
 *
 */
public class GhostPlayer extends BaseTest{
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
        
        Position p = new Position(-1,-1);
        Position p2 = target1;
        
        TestGameBoard logic = new TestGameBoard(server, board);

        BotClient bot1 = new BotClient(clientConnection, moveSpeed, PlayerTypes.PLAYER);
        bot1.waitForConnection();
        bot1.setCommands(commands1);

        BotClient bot2 = new BotClient(clientConnection2, moveSpeed, PlayerTypes.MONSTER);
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

        //Verify that player is dead or not
        assertEquals("Check that bot1 is dead", true, logic.isPlayerDead(bot1id));
        assertEquals("Check that bot 2 is not dead", false, logic.isPlayerDead(bot2id));  
        
        //Verify player positions
        assertEquals("Check starting position of bot 1", p , logic.getPlayerPosition(bot1id));
        assertEquals("Check starting position of bot 2", p2, logic.getPlayerPosition(bot2id));
    }

}
