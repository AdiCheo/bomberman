package carleton.sysc3303.server.connection;

import java.net.InetAddress;
import java.util.Date;

public abstract class AbstractClient implements IClient
{
    protected InetAddress address;
    protected int port;
    protected int id;
    protected Date lastActive;


    /**
     * Constructor.
     *
     * @param id
     * @param address
     * @param port
     */
    public AbstractClient(int id, InetAddress address, int port)
    {
        this.id = id;
        this.address = address;
        this.port = port;
        this.lastActive = new Date(0);
    }


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


    @Override
    public synchronized Date getLastActive()
    {
        return lastActive;
    }


    @Override
    public synchronized void setLastActive(Date date)
    {
        lastActive = date;
    }
}
