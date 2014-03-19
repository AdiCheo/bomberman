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
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.GameBoard;
import carleton.sysc3303.server.ServerBoard;
import carleton.sysc3303.testing.client.TestConnection;
import carleton.sysc3303.testing.server.TestGameBoard;

/**
 * test case to test player bombing
 * @author Nick Mancuso
 *
 */
public class PlayerBombs extends BaseTest{
    private List<String> commands1, commands2;
    private ServerBoard board;
    private Position target1, target2, wall, destructable, exit;
    TestConnection clientConnection2;

    @Before
    public void setUp()
    {
        super.setUp();
        clientConnection2 = new TestConnection(server);
        commands1 = new ArrayList<String>();
        commands2 = new ArrayList<String>();
        board = new ServerBoard(20);
        target1 = new Position(6, 4);
        target2 = new Position(6, 6);
        wall = new Position(5,5);
        destructable = new Position(7,5);
        exit = new Position(6,3);

        commands1.add("DOWN");
        commands1.add("UP");
        commands1.add("UP");
        commands1.add("BOMB");
        commands1.add("Down");

        commands2.add("UP");
        commands2.add("DOWN");

        board.addStartingPosition(target1);
        board.addStartingPosition(target2);
        board.setTile(wall, Tile.WALL);
        board.setTile(destructable, Tile.DESTRUCTABLE);
        board.setTile(exit, Tile.EXIT);
    }

    @Test(timeout = 10000)
    public void test() throws InterruptedException
    {
        server.run();
        clientConnection.run();
        clientConnection2.run();

        Position p = new Position(-1,-1);

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

        // bomb should explode by this point
        Thread.sleep(logic.getConfig().bombTimer);

        //Verify if the player and monster are dead or not
        assertEquals("Check that bot1 is dead", true, logic.isPlayerDead(bot1id));
        assertEquals("Check that bot 2 is not dead", true, logic.isPlayerDead(bot2id));

        //Verify player positions
        assertEquals("Check starting position of bot 1", p, logic.getPlayerPosition(bot1id));
        assertEquals("Check starting position of bot 2", p, logic.getPlayerPosition(bot2id));

        //Verify if the wall is still standing, and that the destructable tile is empty
        assertEquals("Check the wall is still standing", Tile.WALL, board.getTile(wall));
        assertEquals("Check the wall is still standing", Tile.EMPTY, board.getTile(destructable));

        //Check the exit as been revealed
        assertEquals("Checks if the exit as been revealed", false, board.isExitHidden());
    }

}
