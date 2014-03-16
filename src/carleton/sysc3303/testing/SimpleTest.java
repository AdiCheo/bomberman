package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import carleton.sysc3303.server.*;
import carleton.sysc3303.testing.client.*;
import carleton.sysc3303.testing.server.TestServer;

@RunWith(JUnit4.class)
public class SimpleTest
{
    private TestServer server;
    private TestConnection clientConnection;


    @Before
    public void setUp()
    {
        server = new TestServer();
        clientConnection = new TestConnection(server);
    }


    @Test(timeout = 1000)
    public void test()
    {
        //new Thread(server).start();
        server.run();
        clientConnection.run();

        new GameBoard(server, new ServerBoard(20));
        DisconnectingClient client = new DisconnectingClient(clientConnection);

        assertEquals(true, client.isConnected());
        clientConnection.exit();
        assertEquals(false, client.isConnected());
    }


    @After
    public void tearDown()
    {
        server.exit();
    }
}
