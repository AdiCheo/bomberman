package carleton.sysc3303.server.connection;

import java.net.InetAddress;

public abstract class AbstractClient implements IClient
{
    protected InetAddress address;
    protected int port;
    protected int id;


    @Override
    public InetAddress getAddress()
    {
        return address;
    }


    @Override
    public int getPort()
    {
        return port;
    }


    @Override
    public int getId()
    {
        return id;
    }
}
