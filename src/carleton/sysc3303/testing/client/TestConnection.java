package carleton.sysc3303.testing.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.IMessageFactory;
import carleton.sysc3303.testing.server.TestServer;

public class TestConnection extends AbstractConnection
{
    protected static int counter = 0;

    protected TestServer server;
    protected int id;


    /**
     * Constructor.
     *
     * @param server
     */
    public TestConnection(TestServer server)
    {
        super();
        this.server = server;
        this.id = counter++;

        server.registerConnection(getHostname(), getPort(), this);
    }


    /**
     * Gets the current connection's port.
     * @return
     */
    public int getPort()
    {
        return 1024 + id;
    }


    /**
     * Gets the current connection's hostname.
     *
     * @return
     */
    public InetAddress getHostname()
    {
        try
        {
            return InetAddress.getByName("localhost");
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Gets the connection id.
     *
     * @return
     */
    public int getId()
    {
        return id;
    }


    /**
     * Receives a message from the server.
     */
    public void receiveMessage(byte[] data)
    {
        try
        {
            processMessage(IMessageFactory.forge(data));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void sendMessage(IMessage m)
    {
        byte[] data = IMessageFactory.serialize(m);
        server.receiveMessage(getHostname(), getPort(), data);
    }
}
