package carleton.sysc3303.server.connection;

import java.io.IOException;
import java.net.*;

public class UDPClient extends AbstractClient
{
    private byte[] buffer;
    DatagramSocket server;

    public UDPClient(DatagramSocket server, int id, InetAddress address, int port)
    {
        this.id = id;
        this.address = address;
        this.port = port;
        this.server = server;
        this.buffer = new byte[1000]; // TODO: un-hardcode this
    }


    @Override
    public void sendMessage(byte[] data)
    {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

        try
        {
            server.send(packet);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
