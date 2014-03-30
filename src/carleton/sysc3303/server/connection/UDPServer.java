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
    private int bufferSize, sendFrequency;
    private int connection_counter;
    private DatagramSocket serverSocket;
    private BlockingQueue<DatagramPacket> outgoing;
    private BlockingQueue<DatagramPacket> incoming;

    protected int packetsSent, packetsReceived;


    /**
     * Constructor.
     *
     * @param port
     */
    public UDPServer(int port, int sendFrequency)
    {
        this(port, 1000, sendFrequency);
    }


    /**
     * Alternative constructor.
     *
     * @param port
     * @param buffer_length
     */
    public UDPServer(int port, int bufferSize, int sendFrequency)
    {
        super();

        this.port = port;
        this.bufferSize = bufferSize;
        this.sendFrequency = sendFrequency;
        this.connection_counter = 0;
        this.incoming = new LinkedBlockingQueue<DatagramPacket>();
        this.outgoing = new LinkedBlockingQueue<DatagramPacket>();

        this.packetsSent = 0;
        this.packetsReceived = 0;

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
                    try
                    {
                        List<DatagramPacket> tmp = new ArrayList<DatagramPacket>(outgoing.size());
                        outgoing.drainTo(tmp);

                        for(DatagramPacket p: tmp)
                        {
                            serverSocket.send(p);
                            packetsSent++;
                        }

                        Thread.sleep(sendFrequency);

                        //serverSocket.send(outgoing.take());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        while(stillRunning())
        {
            byte[] buffer = new byte[bufferSize];
            receivePacket = new DatagramPacket(buffer, bufferSize);

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
                packetsReceived++;
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
     * Gets the number of packets that were queued and
     * sent to the clients.
     *
     * @return
     */
    public int getPacketsSent()
    {
        return packetsSent;
    }


    /**
     * Gets the number of packets that were successfully recevied
     * and queued by the system.
     *
     * @return
     */
    public int getPacketsReceived()
    {
        return packetsReceived;
    }


    /**
     * Waits until all packets have been sent.
     *
     * @throws InterruptedException
     */
    public void waitForEmptyBuffer() throws InterruptedException
    {
        // polling isn't great, but it works
        while(!outgoing.isEmpty())
        {
            Thread.sleep(25);
        }
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
        invokeConnectionListeners(c, args);
    }
    
    public void setFrequency(int f)
    {
    	sendFrequency = f;
    }
}
