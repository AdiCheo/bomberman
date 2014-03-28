package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import org.junit.*;

import carleton.sysc3303.client.*;
import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.PlayerTypes;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.*;
import carleton.sysc3303.server.connection.*;
import carleton.sysc3303.testing.server.TestGameBoard;


public class PacketlossTest
{
    private List<BotClient> bots;
    private List<UDPConnection> connections;
    private UDPServer server;
    private ServerBoard board;

    private int port = 9999;
    private int boardSize = 10;
    private int commands = 100;


    @Before
    public void setUp() throws UnknownHostException
    {
        board = new ServerBoard(boardSize);
        server = new UDPServer(port, 50);

        bots = new ArrayList<BotClient>();
        connections = new ArrayList<UDPConnection>();

        List<String> commandList = new ArrayList<String>();
        for(int i=0; i<commands; i++)
        {
            commandList.add("RIGHT");
        }

        int max = Math.min(board.maxSupportedPlayers(), GameBoard.MAX_PLAYERS);

        for(int i=0; i<max; i++)
        {
            UDPConnection conn = new UDPConnection(InetAddress.getByName("localhost"), port);
            BotClient bot = new BotClient(conn, 25, PlayerTypes.PLAYER); // 25ms means spamming the server

            bot.setCommands(new ArrayList<String>(commandList));

            bots.add(bot);
            connections.add(conn);
        }
    }


    @Test
    public void runTest() throws InterruptedException
    {
        TestGameBoard logic = new TestGameBoard(server, board);
        new Thread(server).start();

        System.out.println("poop");

        for(UDPConnection c: connections)
        {
            new Thread(c).start();
        }

        for(BotClient b: bots)
        {
            b.waitForConnection();
        }

        logic.setGameState(State.STARTED);
        System.out.println("connected");

        for(BotClient b: bots)
        {
            b.waitForCompletion();
        }

        server.waitForEmptyBuffer();

        int clientSent = 0, clientReceived = 0;

        for(UDPConnection c: connections)
        {
            clientSent += c.getSentPackets();
            clientReceived += c.getReceivedPackets();
        }

        double pctToClient = (server.getPacketsSent() - clientReceived) * 1.0 / server.getPacketsSent();
        double pctFromClient = (clientSent - server.getPacketsReceived()) * 1.0 / clientSent;

        assertTrue("Most packets received by clients.", pctToClient < 0.01);
        assertTrue("Most packets received by clients.", pctFromClient < 0.01);
    }


    @After
    public void tearDown()
    {
        server.exit();
    }
}
