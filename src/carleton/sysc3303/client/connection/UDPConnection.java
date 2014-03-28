package carleton.sysc3303.client.connection;

import java.io.IOException;
import java.net.*;
import java.util.logging.*;

import carleton.sysc3303.common.connection.*;

public class UDPConnection extends AbstractConnection
{
    private InetAddress address;
    private int ip;
    DatagramSocket socket;
    int receivedPackets = 0, sentPackets = 0;


    /**
     * Constructor.
     *
     * @param address
     * @param ip
     */
    public UDPConnection(InetAddress address, int ip)
    {
        super();

        this.address = address;
        this.ip = ip;
    }


    @Override
    public void run()
    {
        try
        {
            socket = new DatagramSocket();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            return;
        }

        run = true;

        super.run();

        while(run)
        {
            byte[] buffer = new byte[1000];
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            try
            {
                socket.receive(receivePacket);
                receivedPackets++;
                logger.log(Level.FINEST, new String(receivePacket.getData()));
                parseMessage(receivePacket.getData());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void sendMessage(IMessage m)
    {
        byte[] buffer = IMessageFactory.serialize(m);
        logger.log(Level.FINEST, "Sending data (raw): " + buffer);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, ip);

        try
        {
            socket.send(packet);
            sentPackets++;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Gets number of packets that have been sent.
     *
     * @return
     */
    public int getSentPackets()
    {
        return sentPackets;
    }


    /**
     * Gets number of packets that have been received.
     *
     * @return
     */
    public int getReceivedPackets()
    {
        return receivedPackets;
    }
}
