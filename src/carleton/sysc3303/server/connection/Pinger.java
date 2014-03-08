package carleton.sysc3303.server.connection;

import java.util.*;
import java.util.concurrent.*;

import carleton.sysc3303.common.connection.MetaMessage;

public class Pinger implements Runnable
{
    private BlockingQueue<IClient> connections, timingOut;
    private IServer server;
    private long wait, timeout;


    /**
     * Constructor.
     *
     * @param server
     * @param wait		how long to wait to ping
     * @param timeout	how long to wait for a pong response
     */
    public Pinger(IServer server, long wait, long timeout)
    {
        this.server = server;
        this.timeout = timeout;
        this.wait = wait;
        this.connections = new PriorityBlockingQueue<IClient>(1, new DateComparator());
        this.timingOut = new PriorityBlockingQueue<IClient>(1, new DateComparator());

        init();
    }


    /**
     * Initialize the pinger.
     */
    private void init()
    {
        server.addConnectionListener(new ConnectionListener() {
            @Override
            public void connectionChanged(IClient c, boolean connected,
                    boolean isSpectator)
            {
                if(connected)
                {
                    try
                    {
                        connections.put(c);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    connections.remove(c);
                }
            }
        });
    }


    @Override
    public void run()
    {
        // thread that removes stale connections
        new Thread() {
            @Override
            public void run()
            {
                while(true)
                {
                    IClient c;
                    long diff;

                    try
                    {
                        c = timingOut.take();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        return;
                    }

                    synchronized(c)
                    {
                        diff = new Date().getTime() - c.getLastActive().getTime();
                    }

                    try
                    {
                        if(diff < wait)
                        {
                            connections.put(c);
                        }
                        else if(diff < wait + timeout)
                        {
                            timingOut.put(c);

                            if(diff > wait + timeout/3)
                            {
                                ping(c);
                            }

                            Thread.sleep(Math.max(wait - diff, timeout/3));
                        }
                        else
                        {
                            System.out.println("Client should be removed: " + c.getId());
                            server.removeClient(c);
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        return;
                    }

                }
            }
        }.start();

        while(true)
        {
            IClient c;
            long diff;

            try
            {
                c = connections.take();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                return;
            }

            synchronized(c)
            {
                diff = new Date().getTime() - c.getLastActive().getTime();
            }

            try
            {
                if(diff > wait)
                {
                    ping(c);
                    timingOut.put(c);
                }
                else
                {
                    connections.put(c);
                    Thread.sleep(wait - diff);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
                return;
            }
        }
    }


    /**
     * Pings a client.
     *
     * @param c
     */
    private void ping(IClient c)
    {
        server.queueMessage(new MetaMessage(MetaMessage.Type.PING), c);
    }


    /**
     * Comparator class used for comparing the clients by their
     * last active times.

     */
    private class DateComparator implements Comparator<IClient>
    {
        @Override
        public int compare(IClient c1, IClient c2)
        {
            return c1.getLastActive().compareTo(c2.getLastActive());
        }
    }
}
