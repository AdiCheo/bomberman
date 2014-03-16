package carleton.sysc3303.testing.server;

import java.net.InetAddress;

import carleton.sysc3303.server.connection.*;

public class TestClient extends AbstractClient
{
    protected static int counter = 0;


    /**
     * Constructor.
     *
     * @param id
     * @param address
     * @param port
     */
    public TestClient(int id, InetAddress address, int port)
    {
        super(id, address, port);
    }
}
