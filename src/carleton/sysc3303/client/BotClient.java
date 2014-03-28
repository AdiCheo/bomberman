package carleton.sysc3303.client;

import java.io.*;
import java.util.*;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MoveMessage.Direction;


public class BotClient
{
    private IConnection c;
    private boolean run;
    private int delay;
    private PlayerTypes t;
    private List<String> commands;
    private Boolean isConnected;

    /**
     * Constructor.
     *
     * @param c
     * @param command
     */
    public BotClient(IConnection c, int delay, PlayerTypes t)
    {
        this.c = c;
        this.delay = delay;
        this.run = false;
        this.t = t;
        this.isConnected = false;

        init();
    }


    /**
     * Sets the bot's commands from a file.
     *
     * @param f
     * @throws IOException
     */
    public void setCommands(File f) throws IOException
    {
        String line;
        BufferedReader reader;
        commands = new ArrayList<String>();

        reader = new BufferedReader(new FileReader(f));

        while((line = reader.readLine()) != null)
        {
            commands.add(line.trim());
        }

        reader.close();
    }


    /**
     * Sets the bot's commands directly.
     *
     * @param commands
     */
    public void setCommands(List<String> commands)
    {
        this.commands = commands;
    }


    /**
     * Initializes the bot and hook into the server.
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
                    start();
                    break;
                case NOTSTARTED:
                    if(commands != null && commands.size() > 0 && commands.get(0).equals("START"))
                    {
                        commands.remove(0); // get rid of the START
                        c.queueMessage(new StateMessage(StateMessage.State.STARTED));
                    }
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

        initConnection();
    }


    /**
     * Checks if bot is running.
     *
     * @return
     */
    public synchronized boolean isRunning()
    {
        return run;
    }


    /**
     * Blocking call that unblocks once the queue is depleted.
     *
     * @throws InterruptedException
     */
    public void waitForCompletion() throws InterruptedException
    {
        synchronized(commands)
        {
            while(commands.size() > 0)
            {
                commands.wait();
            }
        }
    }


    /**
     * Blocking call that unblocks once the bot connects to the server.
     *
     * @throws InterruptedException
     */
    public void waitForConnection() throws InterruptedException
    {
        synchronized(isConnected)
        {
            while(!isConnected)
            {
                isConnected.wait();
            }
        }
    }


    /**
     * Sets the bot's current state.
     *
     * @param run
     */
    protected synchronized void setRunning(boolean run)
    {
        this.run = run;
        notifyAll();
    }


    /**
     * Sends a message to server requesting to join.
     */
    private void initConnection()
    {
        c.queueMessage(MetaMessage.connectPlayer(t));

        new Thread() {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    return;
                }

                if(!isConnected)
                {
                    System.out.println("Timed out, trying again.");
                    initConnection();
                }
            }
        }.start();
    }


    /**
     * Start executing moves.
     */
    public void start()
    {
        setRunning(true);

        new Thread(new Runnable() {
            @Override
            public void run()
            {
                IMessage m;

                synchronized(commands)
                {
                    while(commands.size() > 0 && isRunning())
                    {
                        String line = commands.remove(0);

                        if (line.equals("BOMB"))
                        {
                            c.queueMessage(new BombPlacedMessage());
                        }
                        else
                        {
                            try
                            {
                                m = new MoveMessage(Direction.valueOf(line));
                            }
                            catch(IllegalArgumentException e)
                            {
                                continue;
                            }

                            c.queueMessage(m);
                        }

                        try
                        {
                            Thread.sleep(delay);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    commands.notifyAll();
                }
            }
        }).start();

    }
}

