package carleton.sysc3303.client.connection;

import java.util.*;
import java.util.concurrent.*;

import carleton.sysc3303.client.connection.ConnectionStatusListener.State;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.BombMessage;
import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.IMessageFactory;
import carleton.sysc3303.common.connection.MapMessage;
import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.common.connection.PosMessage;
import carleton.sysc3303.common.connection.StateMessage;
import carleton.sysc3303.common.connection.MetaMessage.Type;


/**
 * An abstract connection that can be used as a base.
 *
 * @author Kirill Stepanov
 */
public abstract class AbstractConnection implements IConnection
{
    protected List<PositionListener> positionListeners;
    protected List<BombListener> bombListeners;
    protected List<MapListener> mapListeners;
    protected List<ConnectionStatusListener> connectionListeners;
    protected List<GameStateListener> stateListeners;
    protected BlockingQueue<IMessage> messageQueue;
    protected Object messageQueueNotifier;
    protected boolean run;


    protected AbstractConnection()
    {
        messageQueue = new LinkedBlockingQueue<IMessage>();
        messageQueueNotifier = new Object();
        run = false;
    }


    @Override
    public void addPositionListener(PositionListener e)
    {
        positionListeners.add(e);
    }


    @Override
    public void addMapListener(MapListener e)
    {
        mapListeners.add(e);
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
    public void addBombListener(BombListener e)
    {
        bombListeners.add(e);
    }


    @Override
    public void queueMessage(IMessage m)
    {
        try
        {
            messageQueue.put(m);
            System.out.println("Added message to queue");
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
            invokeBombListeners(bm.getPosition(), bm.getSize());
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


    /**
     * Send a message to the server.
     */
    protected abstract void sendMessage(IMessage m);


    /**
     * Invoke all listeners bound to this event.
     *
     * @param obj
     * @param old
     * @param new_
     */
    protected void invokePositionListeners(int obj, Position pos, PlayerTypes type)
    {
        for(PositionListener e: positionListeners)
        {
            e.move(obj, pos, type);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param old
     * @param new_
     */
    protected void invokeBombListeners(Position pos, int size)
    {
        for(BombListener e: bombListeners)
        {
            e.bomb(pos, size);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param blocks
     */
    protected void invokeMapListeners(Board b)
    {
        for(MapListener e: mapListeners)
        {
            e.newMap(b);
        }
    }


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
}
