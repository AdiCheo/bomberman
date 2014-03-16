package carleton.sysc3303.testing;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import carleton.sysc3303.testing.client.TestConnection;
import carleton.sysc3303.testing.server.TestServer;

@RunWith(JUnit4.class)
public abstract class BaseTest
{
    protected TestServer server;
    protected TestConnection clientConnection;
    protected TestConnection clientConnection2;
    protected int moveSpeed = 300;


    @Before
    public void setUp()
    {
        //initializationBarrier = new CyclicBarrier(2);
        server = new TestServer();
        clientConnection = new TestConnection(server);
        clientConnection2 = new TestConnection(server);
    }


    @After
    public void tearDown()
    {
        server.exit();
    }
}
