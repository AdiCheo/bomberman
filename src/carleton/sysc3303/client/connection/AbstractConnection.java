package carleton.sysc3303.client.connection;

import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;

import carleton.sysc3303.client.connection.ConnectionStatusListener.State;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;


/**
 * An abstract connection that can be used as a base.
 *
 * @author Kirill Stepanov
 */
public abstract class AbstractConnection implements IConnection
{
    protected static Logger logger = Logger.getLogger("carleton.sysc3303.client.connection.AbstractConnection");

    protected List<ConnectionStatusListener> connectionListeners;
    protected List<GameStateListener> stateListeners;
    protected List<MessageListener> messageListeners;
    protected List<UserMessageListener> userMessageListeners;

    protected BlockingQueue<IMessage> messageQueue;
    protected Object messageQueueNotifier;
    protected boolean run;
    protected int id;


    /**
     * Constructor.
     */
    protected AbstractConnection()
    {
        messageQueue = new LinkedBlockingQueue<IMessage>();
        messageQueueNotifier = new Object();
        run = false;

        initializeListenerLists();
    }


    /**
     * Initializes the default listener list objects.
     */
    protected void initializeListenerLists()
    {
        connectionListeners = new LinkedList<ConnectionStatusListener>();
        stateListeners = new LinkedList<GameStateListener>();
        messageListeners = new LinkedList<MessageListener>();
        userMessageListeners = new LinkedList<UserMessageListener>();
    }


    @Override
    public void addConnectionStatusListener(ConnectionStatusListener e)
    {
        connectionListeners.add(e);
    }


    @Override
    public void addGameStateListener(GameStateListener e)
    {
        stateListeners.add(e);
    }


    @Override
    public void addMessageListener(MessageListener e)
    {
        messageListeners.add(e);
    }


    @Override
    public void addUserMessageListener(UserMessageListener e)
    {
        userMessageListeners.add(e);
    }


    @Override
    public void queueMessage(IMessage m)
    {
        try
        {
            messageQueue.put(m);
            logger.log(Level.FINER, "Added message to queue");
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * Start the message queue thread.
     */
    public void run()
    {
        new Thread() {
            public void run()
            {
                while(true)
                {
                    try
                    {
                        sendMessage(messageQueue.take());

                        synchronized(messageQueueNotifier)
                        {
                            if(messageQueue.isEmpty())
                            {
                                messageQueueNotifier.notifyAll();
                            }
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    @Override
    public void exit()
    {
        queueMessage(new MetaMessage(MetaMessage.Type.DISCONNECT));

        // wait until the message queue is empty before exiting
        synchronized(messageQueueNotifier)
        {
            while(!messageQueue.isEmpty())
            {
                try
                {
                    messageQueueNotifier.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        invokeConnectionStatusListeners(State.DISCONNECTED);

        run = false;
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
    protected void processMessage(IMessage m)
    {
        if(m instanceof MetaMessage)
        {
            MetaMessage mm = (MetaMessage)m;

            switch(mm.getStatus())
            {
            case ACCEPT:
                invokeConnectionStatusListeners(State.CONNECTED);
                id = Integer.parseInt(mm.getMessage());
                break;
            case DISCONNECT:
            case REJECT:
                invokeConnectionStatusListeners(State.DISCONNECTED);
                break;
            case PING:
                queueMessage(new MetaMessage(Type.PONG, ""));
                break;
            case USER_NOTICE:
                this.invokeUserMessageListeners(mm.getMessage());
                break;
            default:
                // TODO: add more
            }
        }
        else if(m instanceof StateMessage)
        {
            StateMessage sm = (StateMessage)m;
            invokeGameStateListeners(sm.getState());
        }
        else
        {
            invokeMessageListeners(m);
        }
    }


    /**
     * Send a message to the server.
     */
    protected abstract void sendMessage(IMessage m);


    /**
     * Invoke all listeners bound to this event.
     *
     * @param s
     */
    protected void invokeConnectionStatusListeners(State s)
    {
        for(ConnectionStatusListener e: connectionListeners)
        {
            e.statusChanged(s);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param s
     */
    protected void invokeGameStateListeners(StateMessage.State s)
    {
        for(GameStateListener e: stateListeners)
        {
            e.stateChanged(s);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param s
     */
    protected void invokeMessageListeners(IMessage m)
    {
        for(MessageListener e: messageListeners)
        {
            e.newMessage(m);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param s
     */
    protected void invokeUserMessageListeners(String s)
    {
        for(UserMessageListener e: userMessageListeners)
        {
            e.newMessage(s);
        }
    }
}
