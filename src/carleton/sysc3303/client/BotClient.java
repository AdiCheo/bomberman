package carleton.sysc3303.client;

import java.io.*;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.common.connection.MoveMessage.Direction;


public class BotClient
{
    private File commandList;
    private IConnection c;
    private boolean run;
    private int delay;


    /**
     * Constructor.
     *
     * @param c
     * @param command
     */
    public BotClient(IConnection c, File command, int delay)
    {
        this.commandList = command;
        this.c = c;
        this.delay = delay;
        this.run = false;

        init();
    }


    /**
     * Initializes the bot and hook into the server.
     */
    private void init()
    {
        final Object that = this;

        c.addConnectionStatusListener(new ConnectionStatusListener() {
            @Override
            public void statusChanged(State s)
            {
                synchronized(that)
                {
                    run = s == State.CONNECTED;
                }

                start();
            }
        });

        c.addPositionListener(new PositionListener() {
            @Override
            public void move(int object, Position pos)
            {
                // TODO Auto-generated method stub
            }
        });

        c.queueMessage(new MetaMessage(Type.CONNECT, "1"));
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
                String line;
                BufferedReader reader;
                MoveMessage m;

                try
                {
                    reader = new BufferedReader(new FileReader(commandList));
                }
                catch(FileNotFoundException e)
                {
                    e.printStackTrace();
                    return;
                }

                try
                {
                    //Read lines
                    while(isRunning() && (line = reader.readLine()) != null)
                    {
                        try
                        {
                            m = new MoveMessage(Direction.valueOf(line.trim()));
                        }
                        catch(IllegalArgumentException e)
                        {
                            continue;
                        }

                        c.queueMessage(m);
                        Thread.sleep(delay);
                    }
                }
                catch (Exception e)
                {
                    // TODO: don't do a catch-all
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        reader.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }
}

