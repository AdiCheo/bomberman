package carleton.sysc3303.client.connection;

import java.util.*;
import java.util.concurrent.*;

import carleton.sysc3303.client.connection.ConnectionStatusListener.State;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.IMessage;
import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.common.connection.StateMessage;


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
                    System.out.println("Woke up");
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        run = false;
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
