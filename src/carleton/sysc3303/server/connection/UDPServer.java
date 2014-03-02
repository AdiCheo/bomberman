package carleton.sysc3303.server.connection;

import java.io.IOException;
import java.net.*;
import java.util.*;

import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.common.connection.MetaMessage.Type;

public class UDPServer extends AbstractServer
{
    private int port;
    private boolean run = false;
    private int buffer_size;
    private byte[] buffer;
    private int connection_counter;
    private DatagramSocket serverSocket;


    /**
     * Constructor.
     *
     * @param port
     */
    public UDPServer(int port)
    {
        this(port, 1000);
        connectionListeners = new LinkedList<ConnectionListener>();
        messageListeners = new LinkedList<MessageListener>();
        clients = new HashMap<Pair<InetAddress, Integer>, IClient>();
        connection_counter = 0;
    }


    /**
     * Alternative constructor.
     *
     * @param port
     * @param buffer_length
     */
    public UDPServer(int port, int buffer_size)
    {
        this.port = port;
        this.buffer_size = buffer_size;
        this.buffer = new byte[buffer_size];
    }


    @Override
    public void run()
    {
        run = true;
        DatagramPacket receivePacket;

        try
        {
            serverSocket = new DatagramSocket(port);
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            return;
        }


        while(stillRunning())
        {
            receivePacket = new DatagramPacket(buffer, buffer_size);

            try
            {
                serverSocket.receive(receivePacket);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                continue;
            }

            DatagramPacket sendPacket = parseMessage(receivePacket);
            
            //send packet
            try {
    			serverSocket.send(sendPacket);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            
        }


        serverSocket.close();
        // TODO: send a "close" to all clients
    }


    @Override
    public synchronized void exit()
    {
        run = false;
    }


    /**
     * Safely check if we should continue listening.
     *
     * @return
     */
    private synchronized boolean stillRunning()
    {
        return run;
    }


    @Override
    protected synchronized void addClient(InetAddress ip, int port)
    {
        Pair<InetAddress, Integer> key = new Pair<InetAddress, Integer>(ip, port);
        IClient c = new UDPClient(serverSocket, connection_counter++, ip, port);
        clients.put(key, c);
        pushMessage(new MetaMessage(Type.ACCEPT, ""+c.getId()), c);
        invokeConnectionListeners(c, true);
    }
}
