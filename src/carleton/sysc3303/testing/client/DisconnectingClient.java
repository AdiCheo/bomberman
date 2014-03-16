package carleton.sysc3303.testing.client;

import java.util.concurrent.CyclicBarrier;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.connection.MetaMessage;

public class DisconnectingClient
{
    private boolean connected = false;
    private IConnection c;


    /**
     * Constructor.
     *
     * @param c
     */
    public DisconnectingClient(IConnection c, final CyclicBarrier b)
    {
        this.c = c;

        c.addConnectionStatusListener(new ConnectionStatusListener() {
            @Override
            public void statusChanged(State s)
            {
                connected = s == State.CONNECTED;

                if(isConnected())
                {
                    try
                    {
                        b.await();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * Initialize connection.
     */
    public void connect()
    {
        c.queueMessage(new MetaMessage(MetaMessage.Type.CONNECT, "0"));
    }


    /**
     * Checks if the client connected or not.
     *
     * @return
     */
    public boolean isConnected()
    {
        return connected;
    }
}
