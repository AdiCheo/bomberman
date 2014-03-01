package carleton.sysc3303.server.connection;

import java.util.List;
import carleton.sysc3303.common.connection.IMessage;

public abstract class AbstractServer implements IServer
{
    protected List<IClient> clients;
    protected List<ConnectionListener> connectionListeners;
    protected List<MessageListener> messageListeners;


    @Override
    public IClient[] getClients()
    {
        return (IClient[])clients.toArray();
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
       for(IClient c: clients)
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
}
