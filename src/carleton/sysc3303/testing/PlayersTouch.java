package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import carleton.sysc3303.client.BotClient;
import carleton.sysc3303.common.PlayerTypes;
import carleton.sysc3303.common.Position;
import carleton.sysc3303.server.ServerBoard;
import carleton.sysc3303.testing.server.TestGameBoard;

public class PlayersTouch extends BaseTest {
    private List<String> commands1, commands2;
    private ServerBoard board;
    private Position target1, target2;

    @Before
    public void setUp()
    {
        super.setUp();
        commands1 = new ArrayList<String>();
        commands2 = new ArrayList<String>();
        board = new ServerBoard(20);
        target1 = new Position(5, 5);
        target2 = new Position(6, 5);

        commands1.add("DOWN");
        commands2.add("UP");

        board.addStartingPosition(new Position(5, 5));
        board.addStartingPosition(new Position(6, 5));
    }


    @Test(timeout = 500)
    public void test() throws InterruptedException
    {    	
        server.run();
        clientConnection.run();
        clientConnection2.run();

        TestGameBoard logic = new TestGameBoard(server, board);
        BotClient bot1 = new BotClient(clientConnection, moveSpeed, PlayerTypes.PLAYER);
        BotClient bot2 = new BotClient(clientConnection2, moveSpeed, PlayerTypes.PLAYER);

        bot1.waitForConnection();
        bot2.waitForConnection();
        
        //Verify Starting Positions
        /*Position pos = logic.getPlayerPosition(0);
        assertEquals(pos, target1);
        pos = logic.getPlayerPosition(1);
        assertEquals(pos, target2);*/
        
        bot1.setCommands(commands1);
        bot2.setCommands(commands2);
        bot1.start();
        bot2.start();
        bot1.waitForCompletion();
        bot1.waitForCompletion();

        //Verify Collision Was Ignored
        Position pos = logic.getPlayerPosition(0);
        assertEquals(pos, target1);
        pos = logic.getPlayerPosition(1);
        assertEquals(pos, target2);
    }
}
