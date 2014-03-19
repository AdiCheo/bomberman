package carleton.sysc3303.client.connection;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

import carleton.sysc3303.common.connection.*;

public class UDPConnection extends AbstractConnection
{
    private InetAddress address;
    private int ip;
    DatagramSocket socket;


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
        this.mapListeners = new LinkedList<MapListener>();
        this.connectionListeners = new LinkedList<ConnectionStatusListener>();
        this.positionListeners = new LinkedList<PositionListener>();
        this.stateListeners = new LinkedList<GameStateListener>();
        this.bombListeners = new LinkedList<BombListener>();
        this.powerupListeners = new LinkedList<PowerupListener>();
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
                System.out.println(new String(receivePacket.getData()));
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

        System.out.print("Sending data (raw): ");
        System.out.println(new String(buffer));

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, ip);

        try
        {
            socket.send(packet);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
