package carleton.sysc3303.server.connection;

import java.net.InetAddress;

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
     * Send data to the client.
     *
     * @param data
     */
    public void sendMessage(byte[] data);
}
