package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import carleton.sysc3303.server.*;
import carleton.sysc3303.testing.client.*;
import carleton.sysc3303.testing.server.TestGameBoard;

@RunWith(JUnit4.class)
public class SimpleTest extends BaseTest
{
    @Test(timeout = 100)
    public void test() throws InterruptedException, BrokenBarrierException
    {
        CyclicBarrier barrier = new CyclicBarrier(2);

        server.run();
        clientConnection.run();

        new TestGameBoard(server, new ServerBoard(20));
        DisconnectingClient client = new DisconnectingClient(clientConnection, barrier);

        // client disconnected at start
        assertEquals(false, client.isConnected());

        client.connect();
        barrier.await(); // wait for response

        assertEquals(true, client.isConnected());
        clientConnection.exit(); // this call blocks
        assertEquals(false, client.isConnected());
    }
}
