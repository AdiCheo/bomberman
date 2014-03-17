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
    public static final int BOMB_TIMEOUT = 3000;
    public static final int BOMB_EXPLODING = 1000;
    public static final int EXPLODE_SIZE = 10;

    protected ServerBoard b;
    protected IServer server;
    protected Map<Integer, Position> player_positions;
    protected Map<Integer, Player> players;
    protected Map<Integer, Position> bombs;
    protected Map<Integer, Position> exploding_bombs;
    protected StateMessage.State current_state;
    protected int currentPlayers;
    protected int explosionCounter = 0;


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
        this.bombs = new HashMap<Integer, Position>();
        this.exploding_bombs = new HashMap<Integer, Position>();
        this.players = new HashMap<Integer, Player>();
        this.current_state = StateMessage.State.NOTSTARTED;
        this.currentPlayers = 0;

        this.b.setPlayers(player_positions);
        this.b.setBombs(bombs);

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
            { synchronized(that) {
                if(current_state == StateMessage.State.END)
                {
                    return;
                }


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
            }}
        });

        server.addMessageListener(new MessageListener() {
            @Override
            public void newMessage(IClient c, IMessage m)
            { synchronized(that) {
                if(current_state == StateMessage.State.END)
                {
                    return;
                }

                handleMessage(c, m);
            }}
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
        Position pos = null;
        if(currentPlayers == MAX_PLAYERS && !isMonster)
        {
            server.queueMessage(new MetaMessage(Type.REJECT, "Server is full"), c);
            return;
        }

        Player p;

        if(isMonster)
        {
            p = new Monster(c.getId());
            // pos = b.getEmptyPosition();
        }
        else
        {
            p = new Player(c.getId());
            currentPlayers++;
            // pos = b.getNextPosition();
        }

        players.put(p.getId(), p);

        // new player position
        pos = b.getNextPosition();

        System.out.printf("Player %d starts at %s\n", c.getId(), pos);
        setPlayerPosition(c.getId(), pos);

        if(current_state == StateMessage.State.STARTED)
        {
            // send player the initial state
            sendInitialState(c);

            // notify everyone of new player
            server.queueMessage(new PosMessage(c.getId(), pos, p.getType()));
        }
        else
        {
            server.queueMessage(new StateMessage(current_state), c);
        }
    }


    /**
     * Sends everything we got to newly connected clients.
     *
     * @param c
     */
    private void sendInitialState(IClient c)
    {
        // send the current state
        server.queueMessage(new StateMessage(current_state), c);

        // send the map
        server.queueMessage(new MapMessage(b.createSendableBoard()), c);

        // send everyone's current positions
        for(Entry<Integer, Position> e: player_positions.entrySet())
        {
            Position pos = e.getValue();
            Player p = players.get(e.getKey());
            server.queueMessage(new PosMessage(e.getKey(), pos, p.getType()), c);
        }

        synchronized(exploding_bombs)
        {
            for(Entry<Integer, Position> e: bombs.entrySet())
            {
                server.queueMessage(new BombMessage(e.getValue(), 0), c);
            }

            for(Entry<Integer, Position> e: exploding_bombs.entrySet())
            {
                server.queueMessage(new BombMessage(e.getValue(), EXPLODE_SIZE), c);
            }
        }
    }


    /**
     * Sends everything we got to everyone.
     */
    private void sendInitialState()
    {
        // send the current state
        server.queueMessage(new StateMessage(current_state));

        // send the map
        server.queueMessage(new MapMessage(b.createSendableBoard()));

        // send everyone's current positions
        for(Entry<Integer, Position> e: player_positions.entrySet())
        {
            Position pos = e.getValue();
            Player p = players.get(e.getKey());
            server.queueMessage(new PosMessage(e.getKey(), pos, p.getType()));
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
     * Handle messages from clients.
     *
     * @param c
     * @param m
     */
    private void handleMessage(IClient c, IMessage m)
    {
        Player p = players.get(c.getId());

        if(p.isDead())
        {
            System.out.printf("Player %d tried to do something, but they are dead.\n", c.getId());
            return;
        }

        if(m instanceof MoveMessage)
        {
            handleMove(c, (MoveMessage)m);
        }
        else if(m instanceof BombPlacedMessage)
        {
            handleBomb(c);
        }
        else if(m instanceof StateMessage)
        {
            handleStateMessage(c, (StateMessage)m);
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

        Position newPosition = new Position(x, y);

        // regular players
        if(!(p instanceof Monster))
        {
            // checks if they can move where they want to move
            if(b.isPositionValid(x, y) &&
               b.isEmpty(x, y) &&
               !b.isOccupied(x, y) &&
               !b.hasBomb(x, y))
            {
                // player was dumb
                if(withinExplosionRange(newPosition))
                {
                    System.out.printf("Player %d walked into an explosion.\n", c.getId());
                    killPlayer(c.getId());
                    return;
                }

                setPlayerPosition(c.getId(), newPosition);
                server.queueMessage(new PosMessage(c.getId(), x, y, p.getType()));

                // reveal the exit if it's hidden
                if(b.isExitHidden() && b.isExit(x, y))
                {
                    System.out.println("Found exit");
                    b.setExitHidden(false);
                    server.queueMessage(new MapMessage(b.createSendableBoard()));
                }
                // end the game if they stepped onto a visible exit
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
            // checks if they can go where they want to go
            if(b.isPositionValid(x,y) &&
               b.isEmpty(x, y) &&
               !b.hasBomb(x, y))
            {
                try
                {
                    int target_id = b.playerAt(x, y);
                    Player target = players.get(target_id);

                    if(target instanceof Monster)
                    {
                        System.out.println("A monster tried to step on another monster.");
                        return;
                    }
                    else
                    {
                        System.out.printf("Player %d got killed by a monster.\n", target_id);
                        killPlayer(target_id);
                        System.out.printf("Player %d should be dead.\n", target_id);
                    }
                }
                catch(RuntimeException e){}

                setPlayerPosition(c.getId(), new Position(x,y));
                server.queueMessage(new PosMessage(c.getId(), x, y, p.getType()));
            }
            else
            {
                System.out.printf(
                        "Monster %d tried to move from %s to (%d,%d), but failed\n",
                        c.getId(),pos, x, y);
            }

        }
    }


    /**
     * Handles newly placed bombs by players.
     *
     * @param c
     */
    private void handleBomb(IClient c)
    {
        Position pos = player_positions.get(c.getId());
        Player p = players.get(c.getId());

        if(!p.decrementRemainingBombs())
        {
            System.out.printf("Player %d tried to place a bomb, but they can't\n", p.getId());
            return;
        }

        System.out.printf("Player %d placed a bomb as %s\n", p.getId(), pos);

        final Object that = this;
        Bomb b = new Bomb(c.getId(), BOMB_TIMEOUT);
        b.setListener(new BombExplodedListener() {
            @Override
            public void bombExploded(int owner, int bomb)
            { synchronized(that) {
                handleBombExplode(owner, bomb);
            }}
        });

        bombs.put(b.getId(), pos);

        server.queueMessage(new BombMessage(pos, 0));

        new Thread(b).start(); // start and background the bomb
    }


    /**
     * Handles state messages from clients.
     *
     * @param c
     * @param m
     */
    private void handleStateMessage(IClient c, StateMessage m)
    {
        // clients can't change state when game not started
        if(current_state != StateMessage.State.NOTSTARTED)
        {
            return;
        }

        if(m.getState() == StateMessage.State.STARTED &&
           current_state == StateMessage.State.NOTSTARTED)
        {
            startGame();
        }
    }


    /**
     * Initializes the game.
     */
    protected void startGame()
    {
        current_state = StateMessage.State.STARTED;
        sendInitialState();
    }


    /**
     * Handles the bomb's explosion.
     *
     * @param owner
     * @param bomb
     */
    private void handleBombExplode(int owner, final int bomb)
    { synchronized(exploding_bombs) {
        final Position p = bombs.get(bomb);
        int x = p.getX(), y = p.getY();

        explosionCounter++;

        // right
        // includes the square with the bomb itself
        for(int i=0; i<EXPLODE_SIZE && handleBombExplodeTile(x + i, y); i++);

        // left
        for(int i=1; i<EXPLODE_SIZE && handleBombExplodeTile(x - i, y); i++);

        // up
        for(int i=1; i<EXPLODE_SIZE && handleBombExplodeTile(x, y + i); i++);

        // down
        for(int i=1; i<EXPLODE_SIZE && handleBombExplodeTile(x, y - i); i++);

        bombs.remove(bomb);

        Player player = players.get(owner);

        // player could have disconnected by the time the bomb explodes
        if(player != null)
        {
            player.incrementRemainingBombs();
        }

        System.out.printf("Player %d's bomb exploded at %s\n", owner, p);

        exploding_bombs.put(bomb, p);

        // in case some blocks were destroyed
        // FIXME: should check if anything changed first
        server.queueMessage(new MapMessage(b.createSendableBoard()));
        server.queueMessage(new BombMessage(p, EXPLODE_SIZE));

        new Thread() {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(BOMB_EXPLODING);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    return;
                }

                // delete the bomb after a timeout
                server.queueMessage(new BombMessage(p, -1));

                synchronized(exploding_bombs)
                {
                    exploding_bombs.remove(bomb);
                }
            }
        }.start();
    }}


    /**
     * Handles the effects of a bomb's explosion at a given tile.
     *
     * @param x
     * @param y
     * @return
     */
    private boolean handleBombExplodeTile(int x, int y)
    {
        // stop when reached edge
        if(!b.isPositionValid(x, y))
        {
            return false;
        }

        // break boxes
        if(b.getTile(x, y) == Tile.DESTRUCTABLE)
        {
            b.setTile(x, y, Tile.EMPTY);
        }


        try
        {
            int id = b.playerAt(x, y);
            killPlayer(id);
            System.out.printf("Player %d should be dead.\n", id);
        }
        // no player at the given tile
        catch(RuntimeException e) {}

        return true;
    }


    /**
     * Checks if a player walks into an explosion.
     *
     * @param p
     * @return
     */
    private boolean withinExplosionRange(Position p)
    { synchronized(exploding_bombs) {

        for(Position bomb: exploding_bombs.values())
        {
            if((bomb.getX() == p.getX() && Math.abs(p.getY() - bomb.getY()) < EXPLODE_SIZE) ||
               (bomb.getY() == p.getY() && Math.abs(p.getX() - bomb.getX()) < EXPLODE_SIZE))
            {
                return true;
            }
        }

        return false;
    }}


    /**
     * Player died or was killed.
     *
     * @param id
     */
    private void killPlayer(int id)
    {
        Player p = players.get(id);
        p.setDead(true);
        player_positions.put(id, new Position(-1,-1));
        server.queueMessage(new PosMessage(id, -1, -1, p.getType()));
    }


    //Returns all adjacent square to a tile, including itself
    protected Position[] getAdjacentTile(int x, int y)
    {
        Position p[] = new Position[5];
        int i = 1;

        p[0] = new Position(x,y);

        if(b.isPositionValid(x+1, y))
        {
            p[i] = new Position(x+1,y);
            i++;
        }

            if(b.isPositionValid(x-1, y))
            {
                p[i] = new Position(x-1, y);
                i++;
            }

                if(b.isPositionValid(x, y+1))
                {
                    p[i] = new Position(x, y+1);
                    i++;
                }

                    if(b.isPositionValid(x, y-1))
                    {
                        p[i] = new Position(x, y-1);
                        i++;
                    }

        return p;
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
