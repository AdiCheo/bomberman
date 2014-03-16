package carleton.sysc3303.client;

import java.io.*;
import java.util.*;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.common.connection.MoveMessage.Direction;


public class BotClient
{
    private IConnection c;
    private boolean run;
    private int delay;
    private PlayerTypes t;
    private List<String> commands;

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
        String clientType;

        if(t == PlayerTypes.PLAYER)
        {
            clientType = "p";
        }
        else
        {
            clientType = "m";
        }

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
                System.out.println(state);
                switch(state)
                {
                case STARTED:
                    run = true;
                    start();
                    break;
                case NOTSTARTED:
                    if(commands.size() > 0 && commands.get(0).equals("START"))
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

        c.queueMessage(new MetaMessage(Type.CONNECT, "1," + clientType));
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
     * Start executing moves.
     */
    public void start()
    {
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                IMessage m;

                for(String line: commands)
                {
                    if (line.equals("BOMB"))
                    {
                        c.queueMessage(new BombPlacedMessage());
                    }
                    else {
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
            }
        }).start();

    }
}

