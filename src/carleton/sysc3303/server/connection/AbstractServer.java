package carleton.sysc3303.server.connection;

import java.lang.reflect.Constructor;
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
    public void queueMessageAll(IMessage m)
    {
       for(IClient c: clients.values())
       {
           queueMessage(m, c);
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
     * @param connected
     * @param isSpectator
     */
    protected void invokeConnectionListeners(IClient c, boolean connected, boolean isSpectator)
    {
        for(ConnectionListener cl: connectionListeners)
        {
            cl.connectionChanged(c, connected, isSpectator);
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
    protected void parseMessage(InetAddress a, int port, byte[] data)
    {
        String[] msg = new String(data).trim().split(":");
        IClient cl = null;
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
            System.out.println(msg[1].length());
            e.printStackTrace();
            return;
        }

        try
        {
            cl = getClient(a, port);
            System.out.println("Message from known client");
        }
        catch(Exception e)
        {
            System.out.println("Message from new client");
        }


        if(m instanceof MetaMessage)
        {
            parseMeta(cl, (MetaMessage)m, a, port);
            return;
        }
        else if(cl != null)
        {
            invokeMessageListener(cl, m);
        }
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
            addClient(ip, port, m.getMessage().equals("0"));
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
     * @param boolean
     */
    protected abstract void addClient(InetAddress ip, int port, boolean isSpectator);


    /**
     * Removes a client.
     *
     * @param cl
     */
    protected synchronized void removeClient(IClient cl)
    {
        Pair<InetAddress, Integer> tmp = new Pair<InetAddress, Integer>(cl.getAddress(), cl.getPort());
        clients.remove(tmp);
        invokeConnectionListeners(cl, false, false); // FIXME
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
