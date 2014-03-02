package carleton.sysc3303.server.connection;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;
import carleton.sysc3303.common.connection.*;


public abstract class AbstractServer implements IServer
{
    protected Map<Pair<InetAddress, Integer>, IClient> clients;
    protected List<ConnectionListener> connectionListeners;
    protected List<MessageListener> messageListeners;


    @Override
    public synchronized IClient[] getClients()
    {
        return (IClient[])clients.values().toArray();
    }


    @Override
    public void pushMessage(IMessage m, IClient c)
    {
        String msg = m.getClass().getCanonicalName() + ":" + m.serialize();
        c.sendMessage(msg.getBytes());
    }


    @Override
    public void pushMessageAll(IMessage m)
    {
       for(IClient c: clients.values())
       {
           pushMessage(m, c);
       }
    }


    @Override
    public void addConnectionListener(ConnectionListener cl)
    {
        connectionListeners.add(cl);
    }


    @Override
    public void addMessageListener(MessageListener ml)
    {
        messageListeners.add(ml);
    }


    /**
     * Emits the new connection event.
     *
     * @param c
     */
    protected void invokeConnectionListeners(IClient c, boolean connected)
    {
        for(ConnectionListener cl: connectionListeners)
        {
            cl.newConnection(c, connected);
        }
    }


    /**
     * Emits the new message event.
     *
     * @param c
     * @param m
     */
    protected void invokeMessageListener(IClient c, IMessage m)
    {
        for(MessageListener ml: messageListeners)
        {
            ml.newMessage(c, m);
        }
    }


    /**
     * Parses messages and invokes the relevant events.
     * TODO: cleanup
     *
     * @param data
     */
    protected DatagramPacket parseMessage(DatagramPacket receivePacket)
    {

    	InetAddress a = receivePacket.getAddress();
		int port = receivePacket.getPort();
		byte[] data = receivePacket.getData();

        String[] msg = new String(data).split(":");
        IClient cl = null;
        @SuppressWarnings("rawtypes")
        Constructor c;
        IMessage m;

        if (msg[0].equalsIgnoreCase("new"))
        {
	        // Testing stuff
	        String response = "Added to game!";
	        byte[] sendData = response.getBytes();

	        //create datagram to send to client
        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, a, port);
            return sendPacket;
        }

        // sample logic for return packet
        if (true)
        {

            cl = getClient(a, port);
            System.out.println("Message from known client");
        }
        catch(Exception e)
        {
            System.out.println("Message from new client");
        }

        return null;



//
//        try
//        {
//            c = Class.forName(msg[0]).getConstructor(new Class[]{String.class});
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return;
//        }
//
//        try
//        {
//            m = (IMessage)c.newInstance(msg[1]);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            return;
//        }
//
//        try
//        {
//            cl = getClient(a, port);
//        }
//        catch(Exception e)
//        {
//            e.printStackTrace();
//        }
//
//
//        if(m instanceof MetaMessage)
//        {
//            parseMeta(cl, (MetaMessage)m, a, port);
//            return;
//        }
//        else if(cl != null)
//        {
//            invokeMessageListener(cl, m);
//        }
    }


    /**
     * Parses messages meant for the server itself.
     * TODO: this is ugly
     *
     * @param cl
     * @param m
     */
    protected void parseMeta(IClient cl, MetaMessage m, InetAddress ip, int port)
    {
        switch(m.getStatus())
        {
        case CONNECT:
            addClient(ip, port);
            break;
        case DISCONNECT:
            removeClient(cl);
            break;
        default:
            System.out.println("Client " + cl.getId() + " sent: " + m.getStatus());
        }
    }


    /**
     * Adds a client to the server.
     *
     * @param ip
     * @param port
     */
    protected abstract void addClient(InetAddress ip, int port);


    /**
     * Removes a client.
     *
     * @param cl
     */
    protected synchronized void removeClient(IClient cl)
    {
        Pair<InetAddress, Integer> tmp = new Pair<InetAddress, Integer>(cl.getAddress(), cl.getPort());
        clients.remove(tmp);
        invokeConnectionListeners(cl, false);
        System.out.println("Client disconnected");
    }


    /**
     * Gets a client based on the address and port.
     * TODO: don't use a plain Exception
     *
     * @param ip
     * @param port
     * @return
     * @throws Exception
     */
    protected IClient getClient(InetAddress ip, int port) throws Exception
    {
        Pair<InetAddress, Integer> tmp = new Pair<InetAddress, Integer>(ip, port);
        IClient cl = clients.get(tmp);

        if(cl == null)
        {
            throw new Exception("Client not found.");
        }

        return cl;
    }


    /**
     * Internal pair implementation since Java doesn't come with one.
     *
     * @param <L>
     * @param <R>
     */
    protected class Pair<L, R>
    {
        private L left;
        private R right;

        public Pair(final L left, final R right)
        {
            this.left = left;
            this.right = right;
        }


        public int hashCode()
        {
            return left.hashCode() ^ right.hashCode();
        }


        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Pair))
            {
                return false;
            }
            if (this == obj)
            {
                return true;
            }

            return left.equals(((Pair) obj).left) && right.equals(((Pair) obj).right);
        }
    }
}