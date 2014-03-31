package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.*;

import org.junit.*;

import carleton.sysc3303.client.*;
import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.PlayerTypes;
import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.*;
import carleton.sysc3303.server.connection.*;
import carleton.sysc3303.server.connection.MessageListener;
import carleton.sysc3303.testing.server.TestGameBoard;


public class PacketlossTest implements MessageListener
{
    private List<BotClient> bots;
    private List<UDPConnection> connections;
    private UDPServer server;
    private ServerBoard board;

    private int port = 9999;
    private int boardSize = 10;
    private int commands = 100;
    private int serverResolution = 50;

    private Integer pingLock;


    @Before
    public void setUp() throws UnknownHostException
    {
        //Logger.getGlobal().setLevel(Level.WARNING);
        for(Handler h: Logger.getLogger("").getHandlers())
        {
            h.setLevel(Level.WARNING);
        }

        board = new ServerBoard(boardSize);
        server = new UDPServer(port, serverResolution);

        server.addMessageListener(this);

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

        for(UDPConnection c: connections)
        {
            new Thread(c).start();
        }

        for(BotClient b: bots)
        {
            b.waitForConnection();
        }

        logic.setGameState(State.STARTED);

        long avgPing = 0;
        long minPing = Integer.MAX_VALUE;
        long maxPing = 0;
        List<IClient> clients = server.getClients();
        for(int i=0; i<clients.size(); i++)
        {
            long ping = pingClient(clients.get(i));
            minPing = Math.min(minPing, ping);
            maxPing = Math.max(maxPing, ping);
            avgPing = avgPing + (ping - avgPing)/(i+1);
        }

        assertTrue("Ping time is less than server resolution.", avgPing < serverResolution);
        System.out.printf("PING (ms): avg %d, min %d, max %d\n", avgPing, minPing, maxPing);

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
        assertTrue("Most packets received by server.", pctFromClient < 0.01);

        System.out.printf("Server lost packet %%: %5.2f, client lost packet %% %5.2f\n", pctFromClient, pctToClient);
    }


    /**
     * Checks how long it takes to ping a client.
     *
     * @param c
     * @return  ping time (in ms)
     * @throws InterruptedException
     */
    public long pingClient(IClient c) throws InterruptedException
    {
        pingLock = c.getId();
        server.queueMessage(new MetaMessage(Type.PING), c);

        long start = System.nanoTime();

        synchronized(pingLock)
        {
            pingLock.wait(2000);
        }

        return (System.nanoTime() - start)/1000/1000/2;
    }


    @After
    public void tearDown()
    {
        server.exit();
    }


    @Override
    public void newMessage(IClient c, IMessage m)
    {
        if(pingLock.equals(c.getId()) && m instanceof MetaMessage)
        {
            MetaMessage mm = (MetaMessage)m;

            if(mm.getStatus() == Type.PONG)
            {
                synchronized(pingLock)
                {
                    pingLock.notify();
                }
            }
        }
    }
}
