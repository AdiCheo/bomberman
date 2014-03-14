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
    private Map<Integer, Position> player_positions;
    private Map<Integer, Player> players;
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
        this.player_positions = new HashMap<Integer, Position>();
        this.players = new HashMap<Integer, Player>();
        this.current_state = StateMessage.State.NOTSTARTED;

        this.b.setPlayers(player_positions);

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
            public void connectionChanged(IClient c, boolean connected, String args)
            {
                if(current_state == StateMessage.State.END)
                {
                    return;
                }

                synchronized(that)
                {
                    if(connected)
                    {
                        String[] split = args.split(",");

                        if(split[0].equals("0"))
                        {
                            newSpectator(c);
                        }
                        else if(split.length == 2)
                        {
                            addPlayer(c, split[1].equals("m"));
                        }
                        else
                        {
                            System.out.println("Invalid connection message: " + args);
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
    private void addPlayer(IClient c, boolean isMonster)
    {
        if(player_positions.size() == MAX_PLAYERS)
        {
            server.queueMessage(new MetaMessage(Type.REJECT, "Server is full"), c);
            return;
        }

        Player p;

        if(isMonster)
        {
            p = new Monster(c.getId());
        }
        else
        {
            p = new Player(c.getId());
        }

        players.put(p.getId(), p);

        // new player position
        Position pos = getNewPosition();

        System.out.printf("Player %d starts at %s\n", c.getId(), pos);
        setPlayerPosition(c.getId(), pos);

        // send player the initial state
        sendInitialState(c);

        // notify everyone of new player
        server.queueMessage(new PosMessage(c.getId(), pos, p.getType()));
    }


    /**
     * Sends everything we got to newly connected clients.
     *
     * @param c
     */
    private void sendInitialState(IClient c)
    {
        // send the map
        server.queueMessage(new MapMessage(b.createSendableBoard()), c);

        // send everyone's current positions
        for(Entry<Integer, Position> e: player_positions.entrySet())
        {
            Position pos = e.getValue();
            Player p = players.get(e.getKey());
            server.queueMessage(new PosMessage(e.getKey(), pos, p.getType()), c);
        }
    }


    /**
     * Removes a player from the game.
     *
     * @param c
     */
    private void removePlayer(IClient c)
    {
        if(player_positions.containsKey(c.getId()))
        {
            player_positions.remove(c.getId());
            Player p = players.remove(c.getId());
            server.queueMessage(new PosMessage(c.getId(), -1, -1, p.getType()));
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

        } while (b.isOccupied(randomX, randomY) && !b.isEmpty(randomX, randomY));

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
        else if(m instanceof BombPlacedMessage)
        {
            System.out.printf("Player %d placed a bomb.\n", c.getId());
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
        Player p = players.get(c.getId());

        if(!p.canMove())
        {
            System.out.printf("Player %d tried to move too early\n", c.getId());
            return;
        }
        else
        {
            p.setLastMoveTime(new Date());
        }

        Position pos = player_positions.get(c.getId());
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

        if(!(p instanceof Monster))
        {
            if(b.isPositionValid(x, y) && b.isEmpty(x, y) && !b.isOccupied(x, y))
            {
                setPlayerPosition(c.getId(), new Position(x, y));
                server.queueMessage(new PosMessage(c.getId(), x, y, p.getType()));
                /*System.out.printf(
                        "Player %d moved from %s to (%d,%d)\n",
                        c.getId(), pos, x, y);*/

                if(b.isExitHidden() && b.isExit(x, y))
                {
                    System.out.println("Found exit");
                    b.setExitHidden(false);
                    server.queueMessage(new MapMessage(b.createSendableBoard()));
                }
                else if(!b.isExitHidden() && b.isExit(x, y))
                {
                    System.out.println("Game over");
                    current_state = StateMessage.State.END;
                    server.queueMessage(new StateMessage(current_state));
                }
            }
            else
            {
                System.out.printf(
                        "Player %d tried to move from %s to (%d,%d), but failed\n",
                        c.getId(), pos, x, y);
            }
        }
        else
        {
            if(b.isPositionValid(x,y) && b.isEmpty(x, y))
            {
                setPlayerPosition(c.getId(), new Position(x,y));
                server.queueMessage(new PosMessage(c.getId(), x, y, p.getType()));
                /*System.outprintf(
                 * 		"Player %d moved from %s to (%d,%d) \n",
                 * 		c.getId(), pos, x, y);*/

            }
            else
            {
                System.out.printf(
                        "Player %d tried to move from %s to (%d,%d), but failed\n",
                        c.getId(),pos, x, y);
            }

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
        player_positions.put(id, pos);
    }
}
