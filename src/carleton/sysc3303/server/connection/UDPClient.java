package carleton.sysc3303.server.connection;

import java.io.IOException;
import java.net.*;

public class UDPClient extends AbstractClient
{
    DatagramSocket server;

    public UDPClient(DatagramSocket server, int id, InetAddress address, int port)
    {
        this.id = id;
        this.address = address;
        this.port = port;
        this.server = server;
    }


    @Override
    public void sendMessage(byte[] data)
    {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);

        try
        {
            server.send(packet);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
