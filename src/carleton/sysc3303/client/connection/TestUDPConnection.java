package carleton.sysc3303.client.connection;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.*;

import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.common.connection.MetaMessage.Type;

public class TestUDPConnection extends AbstractConnection
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
    public TestUDPConnection(InetAddress address, int ip)
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
        byte[] buffer = new byte[1000];

        while(run)
        {
            DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
            try
            {
                socket.receive(receivePacket);
                System.out.println(new String(receivePacket.getData()));
                parseMessage(receivePacket.getData());
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    protected void parseMessage(byte[] data)
    {
        String[] msg = new String(data).split(":");
        @SuppressWarnings("rawtypes")
        Constructor c;
        IMessage m;

        try
        {
            c = Class.forName(msg[0]).getConstructor(new Class[]{String.class});
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            m = (IMessage)c.newInstance(msg[1]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        if(m instanceof MetaMessage)
        {
            MetaMessage mm = (MetaMessage)m;

            if(mm.getStatus() == Type.ACCEPT)
            {
                System.out.println("Successfully connected");
                System.out.println("Disconnecting...");
                queueMessage(new MetaMessage(Type.DISCONNECT, "bye"));
                run = false;
                return;
            }
        }

        System.out.println("Received garbage.");
    }


    @Override
    protected void sendMessage(IMessage m)
    {
        String msg = m.getClass().getCanonicalName() + ":" + m.serialize();
        byte[] buffer = msg.getBytes();

        System.out.print("Sending data (raw): ");
        System.out.println(msg);

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, ip);

        try
        {
            socket.send(packet);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
