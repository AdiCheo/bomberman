package carleton.sysc3303.server.connection;

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
    protected void invokeConnectionListeners(IClient c)
    {
        for(ConnectionListener cl: connectionListeners)
        {
            cl.newConnection(c);
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
    protected DatagramPacket parseMessage(InetAddress a, int port, byte[] data)
    {
        String[] msg = new String(data).split(":");
        IClient cl = null;
        @SuppressWarnings("rawtypes")
        Constructor c;
        IMessage m;

        if (true)
        {
	        // Testing stuff
	        byte[] sendData = new byte[1024];
	        String capitalizedSentence = msg[0].toUpperCase();
	        sendData = capitalizedSentence.getBytes();
	        
	        //create datagram to send to client
        	DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, a, port);
        	return sendPacket;
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
    }
}
