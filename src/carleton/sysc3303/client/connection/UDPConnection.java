package carleton.sysc3303.client.connection;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

import carleton.sysc3303.client.connection.ConnectionStatusListener.State;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;

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


    /**
     * Parse a message from the server.
     *
     * @param data
     */
    protected void parseMessage(byte[] data)
    {
        try
        {
            processMessage(IMessageFactory.forge(data));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Process message and invoke the relevant events.
     *
     * @param m
     */
    private void processMessage(IMessage m)
    {
        if(m instanceof MapMessage)
        {
            MapMessage mm = (MapMessage)m;
            invokeMapListeners(mm.getBoard());
        }
        else if(m instanceof PosMessage)
        {
            PosMessage pm = (PosMessage)m;
            invokePositionListeners(pm.getPid(), pm.getPosition(), pm.getType());
        }
        else if(m instanceof BombMessage)
        {
            BombMessage bm = (BombMessage)m;
            invokeBombListeners();
        }
        else if(m instanceof MetaMessage)
        {
            MetaMessage mm = (MetaMessage)m;

            switch(mm.getStatus())
            {
            case ACCEPT:
                this.invokeConnectionStatusListeners(State.CONNECTED);
                break;
            case DISCONNECT:
            case REJECT:
                this.invokeConnectionStatusListeners(State.DISCONNECTED);
                break;
            case PING:
                this.queueMessage(new MetaMessage(Type.PONG, ""));
                break;
            default:
                // TODO: add more
            }
        }
        else if(m instanceof StateMessage)
        {
            StateMessage sm = (StateMessage)m;
            this.invokeGameStateListeners(sm.getState());
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
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
