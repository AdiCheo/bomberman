package carleton.sysc3303.testing.server;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.server.connection.*;

/**
 * Class used for testing purposes.
 */
public class TestServer extends AbstractServer
{
    protected int connectionCounter = 0;


    /**
     * Constructor.
     */
    public TestServer()
    {
        super();

        this.connectionListeners = new LinkedList<ConnectionListener>();
        this.messageListeners = new LinkedList<MessageListener>();
        this.clients = new HashMap<Pair<InetAddress, Integer>, IClient>();
    }

    @Override
    public void exit()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void queueMessage(IMessage m, IClient c)
    {
        System.out.println("Have a message for client.");
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub
    }


    /**
     * Pretends a some data has arrived.
     *
     * @param address
     * @param port
     * @param data
     */
    public synchronized void receiveMessage(InetAddress address, int port, byte[] data)
    {
        System.out.println("Received message from client.");
        parseMessage(address, port, data);
    }


    @Override
    protected void addClient(InetAddress ip, int port, String args)
    {
        Pair<InetAddress, Integer> key = new Pair<InetAddress, Integer>(ip, port);
        IClient c = new TestClient(connectionCounter++, ip, port);
        c.setLastActive(new Date());

        clients.put(key, c);
        queueMessage(new MetaMessage(Type.ACCEPT, "" + c.getId()), c);
        invokeConnectionListeners(c, true, args);
    }
}
