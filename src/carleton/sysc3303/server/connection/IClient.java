package carleton.sysc3303.server.connection;

import java.net.InetAddress;
import java.util.Date;

public interface IClient
{
    /**
     * Gets the client's IP address
     *
     * @return
     */
    public InetAddress getAddress();


    /**
     * Gets the port the client is connected to.
     *
     * @return
     */
    public int getPort();


    /**
     * Gets the client's unique id.
     *
     * @return
     */
    public int getId();


    /**
     * Gets the last time the client was active.
     *
     * @return
     */
    public Date getLastActive();


    /**
     * Sets the last time the client was active.
     *
     * @param date
     */
    public void setLastActive(Date date);
}
