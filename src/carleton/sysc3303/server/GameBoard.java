package carleton.sysc3303.server;


import java.util.*;
import java.util.Map.Entry;

import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.server.connection.*;


public class GameBoard
{
    public static final int MAX_PLAYERS = 4;

    private ServerBoard b;
    private IServer server;
    private Map<Integer, Position> players;
    private Queue<Character> letters;
    private StateMessage.State current_state;


    /**
     * Constructor.
     *
     * @param server
     * @param size		Board size, always a square
     */
    public GameBoard(IServer server, ServerBoard b)
    {
        init(server, b);
    }


    /**
     * Initializes the data and hook into the server.
     *
     * @param server
     * @param tiles
     */
    private void init(IServer server, ServerBoard b)
    {
        this.server = server;
        this.b = b;
        this.players = new HashMap<Integer, Position>();
        this.letters = new ArrayDeque<Character>();
        this.current_state = StateMessage.State.NOTSTARTED;

        System.out.println(b);

        this.b.setPlayers(players);

        for(char i='a'; i<'z'; i++)
        {
            letters.add(i);
        }

        hookEvents();
    }


    /**
     * Hook into the connection.
     */
    private void hookEvents()
    {
        final Object that = this;

        server.addConnectionListener(new ConnectionListener() {
            @Override
            public void connectionChanged(IClient c, boolean connected, boolean isSpectator)
            {
                if(current_state == StateMessage.State.END)
                {
                    return;
                }

                synchronized(that)
                {
                    if(connected)
                    {
                        if(isSpectator)
                        {
                            newSpectator(c);
                        }
                        else
                        {
                            addPlayer(c);
                        }
                    }
                    else
                    {
                        removePlayer(c);
                    }
                }
            }
        });

        server.addMessageListener(new MessageListener() {
            @Override
            public void newMessage(IClient c, IMessage m)
            {
                if(current_state == StateMessage.State.END)
                {
                    return;
                }

                synchronized(that)
                {
                    handleMessage(c, m);
                }
            }
        });
    }



    /**
     * What to do with new spectator.
     *
     * @param c
     */
    private void newSpectator(IClient c)
    {
        sendInitialState(c);
    }


    /**
     * Adds a player
     *
     * @param c
     */
    private void addPlayer(IClient c)
    {
        if(players.size() == MAX_PLAYERS)
        {
            server.pushMessage(new MetaMessage(Type.REJECT, "Server is full"), c);
            return;
        }

        // new player position
        Position pos = getNewPosition();

        System.out.printf("Player %d starts at (%d,%d)\n", c.getId(), pos.getX(), pos.getY());
        setPlayerPosition(c.getId(), pos);

        // send player the initial state
        sendInitialState(c);

        // notify everyone of new player
        server.pushMessageAll(new PosMessage(c.getId(), pos.getX(), pos.getY()));
    }


    /**
     * Sends everything we got to newly connected clients.
     *
     * @param c
     */
    private void sendInitialState(IClient c)
    {
        // send the map
        server.pushMessage(new MapMessage(b.createSendableBoard()), c);

        // send everyone's current positions
        for(Entry<Integer, Position> e: players.entrySet())
        {
            Position pos = e.getValue();
            server.pushMessage(new PosMessage(e.getKey(), pos.getX(), pos.getY()), c);
        }
    }


    /**
     * Removes a player from the game.
     *
     * @param c
     */
    private void removePlayer(IClient c)
    {
        if(players.containsKey(c.getId()))
        {
            players.remove(c.getId());
            server.pushMessageAll(new PosMessage(c.getId(), -1, -1));
        }
    }


    /**
     * Get a starting position for a new player.
     * TODO: make it random but smart.
     *
     * @return
     */
    private Position getNewPosition()
    {
        int randomX;
        int randomY;
        Random randomGenerator = new Random();

        System.out.println("Getting new player pos");

        // Find available start location
        do
        {
            randomX = randomGenerator.nextInt(b.getSize());
            randomY = randomGenerator.nextInt(b.getSize());

        } while (b.isOccupied(randomX, randomY));

        return new Position(randomX, randomY);
    }


    /**
     * Handle messages from clients.
     *
     * @param c
     * @param m
     */
    private void handleMessage(IClient c, IMessage m)
    {
        if(m instanceof MoveMessage)
        {
            handleMove(c, (MoveMessage)m);
        }
    }


    /**
     * Handle movement from a client.
     *
     * @param c
     * @param m
     */
    private void handleMove(IClient c, MoveMessage m)
    {
        Position pos = players.get(c.getId());
        int x = pos.getX(), y = pos.getY();

        switch(m.getDirection())
        {
        case UP:
            y++;
            break;
        case DOWN:
            y--;
            break;
        case LEFT:
            x--;
            break;
        case RIGHT:
            x++;
        }

        if(b.isPositionValid(x, y) && !b.isOccupied(x, y))
        {
            setPlayerPosition(c.getId(), new Position(x, y));
            server.pushMessageAll(new PosMessage(c.getId(), x, y));
            System.out.printf(
                    "Player %d moved from (%d,%d) to (%d,%d)\n",
                    c.getId(), pos.getX(), pos.getY(), x, y);

            if(b.isExitHidden() && b.isExit(x, y))
            {
                System.out.println("Found exit");
                b.setExitHidden(false);
                server.pushMessageAll(new MapMessage(b.createSendableBoard()));
            }
            else if(!b.isExitHidden() && b.isExit(x, y))
            {
                System.out.println("Game over");
                current_state = StateMessage.State.END;
                server.pushMessageAll(new StateMessage(current_state));
            }
        }
        else
        {
            System.out.printf(
                    "Player %d tried to move from (%d,%d) to (%d,%d), but failed\n",
                    c.getId(), pos.getX(), pos.getY(), x, y);
        }
    }


    /**
     * Set a player's position.
     *
     * @param p
     * @param pos
     */
    private void setPlayerPosition(int id, Position pos)
    {
        players.put(id, pos);
    }


    /**
     * Checks if two positions are adjacent to each other
     *
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @return
     */
    public boolean isAdjacent(int x, int y, int x2, int y2)
    {
        return (Math.abs(x - x2) == 1 && Math.abs(y - y2) == 0) ||
                (Math.abs(x - x2) == 0 && Math.abs(y - y2) == 1);
    }
}
