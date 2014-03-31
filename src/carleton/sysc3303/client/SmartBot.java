package carleton.sysc3303.client;

import java.util.*;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MoveMessage.Direction;
import carleton.sysc3303.common.connection.StateMessage.State;

public class SmartBot implements GameStateListener,
                                 MessageListener,
                                 IdListener
{
    private IConnection c;
    private int timer;
    private Board b;
    private boolean isRunning;
    private Object stateLock;
    private Map<Integer, Position> players;
    private Position myPosition;
    private int myId = -1;


    /**
     * Constructor.
     *
     * @param c
     */
    public SmartBot(IConnection c, int timer)
    {
        this.c = c;
        this.timer = timer;
        this.stateLock = new Object();
        this.isRunning = false;
        this.players = new HashMap<Integer, Position>();

        init();
    }


    /**
     * Initializes the bot.
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

        c.addGameStateListener(this);
        c.addMessageListener(this);
        c.addIdListener(this);

        c.queueMessage(MetaMessage.connectPlayer(PlayerTypes.MONSTER));
    }


    @Override
    public void stateChanged(State state)
    { synchronized(stateLock) {
        if(!isRunning && state == State.STARTED)
        {
            isRunning = true;
            start();
        }
    }}


    /**
     * Starts the bot.
     */
    private void start()
    {
        new Thread() {
            @Override
            public void run()
            {
                while(true)
                {
                    boolean run;

                    synchronized(stateLock)
                    {
                        run = isRunning;
                    }

                    if(!run)
                    {
                        return;
                    }

                    Position p = findTarget();
                    Direction d;

                    if(p != null)
                    {

                        if(myPosition.getX() == p.getX())
                        {
                            d = myPosition.getY() > p.getY() ? Direction.DOWN : Direction.UP;
                        }
                        else
                        {
                            d = myPosition.getX() > p.getX() ? Direction.LEFT : Direction.RIGHT;
                        }
                    }
                    else
                    {
                        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
                        d = directions[(int)(Math.random() * directions.length)];
                    }

                    c.queueMessage(new MoveMessage(d));

                    try
                    {
                        Thread.sleep(timer);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }.start();
    }


    @Override
    public void newMessage(IMessage m)
    {
        if(m instanceof MapMessage)
        {
            b = ((MapMessage)m).getBoard();
        }
        else if(m instanceof PlayerMessage)
        {
            handlePlayerMessage((PlayerMessage)m);
        }
    }


    /**
     * Updates player list.
     *
     * @param m
     */
    private void handlePlayerMessage(PlayerMessage m)
    { synchronized(players) {
        Position p = m.getPosition();

        if(m.getType() == PlayerTypes.PLAYER)
        {
            if(p.getX() < 0 || p.getY() < 0)
            {
                players.remove(m.getPid());
            }
            else
            {
                players.put(m.getPid(), p);
            }
        }
        else if(m.getPid() == myId)
        {
            myPosition = p;

            // bot died
            if(myPosition.getX() < 0 || myPosition.getY() < 0)
            {
                synchronized(stateLock)
                {
                    isRunning = false;
                }
            }
        }
    }}


    /**
     * Finds a player to target.
     *
     * @return
     */
    private Position findTarget()
    { synchronized(players) {
        if(myPosition == null)
        {
            return null;
        }

        Position target = null;
        Integer distance = null;

        for(Position p: players.values())
        {
            if((p.getX() == myPosition.getX() || p.getY() == myPosition.getY()) && walkable(p, myPosition))
            {
                if(target == null)
                {
                    target = p;

                    if(p.getX() == myPosition.getX())
                    {
                        distance = Math.abs(p.getX() - myPosition.getX());
                    }
                    else
                    {
                        distance = Math.abs(p.getY() - myPosition.getY());
                    }
                }
                else if(p.getX() == myPosition.getX() && distance > Math.abs(p.getX() - myPosition.getX()))
                {
                    distance = Math.abs(p.getX() - myPosition.getX());
                    target = p;
                }
                else if(distance > Math.abs(p.getY() - myPosition.getY()))
                {
                    distance = Math.abs(p.getY() - myPosition.getY());
                    target = p;
                }
            }
        }

        return target;
    }}


    /**
     * Check that bot can walk between the two positions.
     *
     * @param p1
     * @param p2
     * @return
     */
    private boolean walkable(Position p1, Position p2)
    {
        if(p1.getX() == p2.getX())
        {
            int start = Math.min(p1.getY(), p2.getY());
            int end = Math.max(p1.getY(), p2.getY());

            for(int i=start; i<end; i++)
            {
                if(!b.isEmpty(i, p1.getX()))
                {
                    return false;
                }
            }

            return true;
        }
        else if(p1.getY() == p2.getY())
        {
            int start = Math.min(p1.getX(), p2.getX());
            int end = Math.max(p1.getX(), p2.getX());

            for(int i=start; i<end; i++)
            {
                if(!b.isEmpty(p1.getX(), i))
                {
                    return false;
                }
            }

            return true;
        }
        else
        {
            return false;
        }
    }


    @Override
    public void setId(int id)
    {
        myId = id;
    }
}
