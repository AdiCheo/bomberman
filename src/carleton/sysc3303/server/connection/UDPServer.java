package carleton.sysc3303.server.connection;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;

public class UDPServer extends AbstractServer
{
    private int port;
    private boolean run = false;
    private int buffer_size;
    private int connection_counter;
    private DatagramSocket serverSocket;
    private BlockingQueue<DatagramPacket> outgoing;
    private BlockingQueue<DatagramPacket> incoming;


    /**
     * Constructor.
     *
     * @param port
     */
    public UDPServer(int port)
    {
        this(port, 1000);
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
        this.connectionListeners = new LinkedList<ConnectionListener>();
        this.messageListeners = new LinkedList<MessageListener>();
        this.clients = new HashMap<Pair<InetAddress, Integer>, IClient>();
        this.connection_counter = 0;
        this.incoming = new LinkedBlockingQueue<DatagramPacket>();
        this.outgoing = new LinkedBlockingQueue<DatagramPacket>();

        new Thread(new Pinger(this, 30000, 5000)).start();
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

        // Thread responsible for getting incoming packets
        // and pushing them through the system
        new Thread() {
            public void run()
            {
                while(true)
                {
                    DatagramPacket p;

                    try
                    {
                        p = incoming.take();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        continue;
                    }

                    parseMessage(p.getAddress(), p.getPort(), p.getData());
                }
            }
        }.start();

        // Thread responsible for taking queued messages
        // and sending them to the recipients.
        new Thread() {
            public void run()
            {
                while(true)
                {
                    DatagramPacket p;

                    try
                    {
                        p = outgoing.take();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        continue;
                    }

                    try
                    {
                        serverSocket.send(p);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        while(stillRunning())
        {
            byte[] buffer = new byte[buffer_size];
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

            try
            {
                incoming.put(receivePacket);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        serverSocket.close();
        // TODO: send a "close" to all clients
    }


    @Override
    public void queueMessage(IMessage m, IClient c)
    {
        byte[] data = IMessageFactory.serialize(m);

        try
        {
            outgoing.put(new DatagramPacket(data, data.length, c.getAddress(), c.getPort()));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
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
    protected synchronized void addClient(InetAddress ip, int port, String args)
    {
        Pair<InetAddress, Integer> key = new Pair<InetAddress, Integer>(ip, port);
        IClient c = new UDPClient(connection_counter++, ip, port);
        c.setLastActive(new Date());

        clients.put(key, c);
        queueMessage(new MetaMessage(Type.ACCEPT, "" + c.getId()), c);
        invokeConnectionListeners(c, true, args);
    }
}
