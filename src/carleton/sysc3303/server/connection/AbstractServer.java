package carleton.sysc3303.server.connection;

import java.net.InetAddress;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import carleton.sysc3303.common.connection.*;


public abstract class AbstractServer implements IServer
{
    protected static Logger logger = Logger.getLogger("carleton.sysc3303.server.connection.AbstractServer");
    protected Map<Pair<InetAddress, Integer>, IClient> clients;
    protected List<ConnectionListener> connectionListeners;
    protected List<DisconnectionListener> disconnectionListeners;
    protected List<MessageListener> messageListeners;


    /**
     * Constructor.
     */
    protected AbstractServer()
    {
        initializeListenerLists();
    }


    /**
     * Initializes the data structures for storing listeners.
     * Override this if you want to use a different implementation.
     */
    protected void initializeListenerLists()
    {
        connectionListeners = new LinkedList<ConnectionListener>();
        disconnectionListeners = new LinkedList<DisconnectionListener>();
        messageListeners = new LinkedList<MessageListener>();
        clients = new HashMap<Pair<InetAddress, Integer>, IClient>();
    }


    @Override
    public void queueMessage(IMessage m)
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
    public void addDisconnectionListener(DisconnectionListener cl)
    {
        disconnectionListeners.add(cl);
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
     * @param args	String args from the client, if any
     */
    protected void invokeConnectionListeners(IClient c, String args)
    {
        for(ConnectionListener cl: connectionListeners)
        {
            cl.connected(c, args);
        }
    }


    /**
     * Emits the new disconnection event.
     *
     */
    protected void invokeDisconnectionListeners(IClient c)
    {
        for(DisconnectionListener cl: disconnectionListeners)
        {
            cl.disconnected(c);
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
        IClient cl = null;
        IMessage m;

        try
        {
            m = IMessageFactory.forge(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            cl = getClient(a, port);
            //logger.log(Level.INFO, "Message from known client: " + cl.getId());
            cl.setLastActive(new Date());
        }
        catch(Exception e)
        {
            logger.log(Level.FINE, "Message from new client.");
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
        if(cl == null && m.getStatus() != MetaMessage.Type.CONNECT)
        {
            // silently ignore non-connection messages from unknown clients
            return;
        }

        switch(m.getStatus())
        {
        case CONNECT:
            addClient(ip, port, m.getMessage());
            break;
        case DISCONNECT:
            removeClient(cl);
            break;
        default:
            invokeMessageListener(cl, m);
        }
    }


    /**
     * Adds a client to the server.
     *
     * @param ip
     * @param port
     * @param args
     */
    protected abstract void addClient(InetAddress ip, int port, String args);


    /**
     * Removes a client.
     *
     * @param cl
     */
    public synchronized void removeClient(IClient cl)
    {
        Pair<InetAddress, Integer> tmp = new Pair<InetAddress, Integer>(cl.getAddress(), cl.getPort());
        clients.remove(tmp);
        invokeDisconnectionListeners(cl);
        logger.log(Level.INFO, "Client disconnected.");
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


        @SuppressWarnings("rawtypes")
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
