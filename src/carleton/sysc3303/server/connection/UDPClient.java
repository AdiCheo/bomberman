package carleton.sysc3303.server.connection;

import java.net.*;

public class UDPClient extends AbstractClient
{
    /**
     * Constructor.
     *
     * @param id
     * @param address
     * @param port
     */
    public UDPClient(int id, InetAddress address, int port)
    {
        super(id, address, port);
    }
}
