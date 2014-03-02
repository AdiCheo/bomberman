package carleton.sysc3303.client.connection;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import carleton.sysc3303.client.connection.ConnectionStatusListener.State;
import carleton.sysc3303.common.Position;
import carleton.sysc3303.common.connection.IMessage;


/**
 * An abstract connection that can be used as a base.
 *
 * @author Kirill Stepanov
 */
public abstract class AbstractConnection implements IConnection
{
    private List<PositionListener> positionListeners;
    private List<MapListener> mapListeners;
    private List<ConnectionStatusListener> connectionListeners;
    protected LinkedBlockingQueue<IMessage> messageQueue;
    protected boolean run;


    protected AbstractConnection()
    {
        messageQueue = new LinkedBlockingQueue<IMessage>();
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
    public void queueMessage(IMessage m)
    {
        synchronized(messageQueue)
        {
            messageQueue.add(m);
            System.out.println("Added message to queue");
            messageQueue.notify();
        }
    }


    /**
     * Start the message queue thread.
     */
    public void run()
    {
        new Thread(new Runnable() {
            public void run()
            {
                while(true)
                {
                    synchronized(messageQueue)
                    {
                        while(messageQueue.isEmpty())
                        {
                            try
                            {
                                messageQueue.wait();
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        sendMessage(messageQueue.remove());
                    }
                }
            }
        }).start();
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
    protected void invokePositionListeners(int obj, Position pos)
    {
        for(PositionListener e: positionListeners)
        {
            e.move(obj, pos);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param blocks
     */
    protected void invokeMapListeners(boolean[][] walls)
    {
        for(MapListener e: mapListeners)
        {
            e.newMap(walls);
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
}
