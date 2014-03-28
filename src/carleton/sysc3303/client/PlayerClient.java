package carleton.sysc3303.client;

import java.util.*;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.common.connection.MoveMessage.Direction;

public class PlayerClient
{
	private IConnection c;
	private boolean run;
	private int delay;
	private Boolean isConnected;
	
	/**
	 * Constructor
	 * 
	 * @param c
	 * @param delay
	 */
	public PlayerClient(IConnection c, int delay)
	{
		this.c = c;
		this.delay = delay;
		this.isConnected = false;
		
		init();
	}
	
	/**
	 * Initializes client and hook into the server 
	 */
	private void init()
    {
		Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                c.exit();
            }
        });

        c.addGameStateListener(new GameStateListener() {
            @Override
            public void stateChanged(StateMessage.State state)
            {
                switch(state)
                {
                case STARTED:
                    //play();
                    break;
                case NOTSTARTED:
                    //Add start button in GUI
                    c.queueMessage(new StateMessage(StateMessage.State.STARTED));
                    break;
                case END:
                    System.exit(0);
                }
            }

        });

        c.addConnectionStatusListener(new ConnectionStatusListener() {
            @Override
            public void statusChanged(State s)
            {
                synchronized(isConnected)
                {
                    isConnected.notifyAll();
                    isConnected = s == State.CONNECTED;
                }
            }
        });
        
        c.queueMessage(new MetaMessage(Type.CONNECT, "1,p"));
    }
	
	protected synchronized void setRunning(boolean run)
	{
		this.run = run;
		notifyAll();
	}	
}
