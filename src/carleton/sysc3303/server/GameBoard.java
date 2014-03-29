package carleton.sysc3303.server;

import java.util.*;
import java.util.logging.*;
import java.util.Map.Entry;

import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.common.connection.PowerupMessage.Action;
import carleton.sysc3303.common.connection.StateMessage.State;
import carleton.sysc3303.server.connection.*;


public class GameBoard
{
    public static final int MAX_PLAYERS = 32;
    protected static Logger logger = Logger.getLogger("carleton.sysc3303.server.GameBoard");

    protected Config conf;
    protected ServerBoard b;
    protected IServer server;
    protected Map<Integer, Position> playerPositions;
    protected Map<Integer, Player> players;
    protected Map<Integer, Position> bombs;
    protected Map<Integer, Position> explodingBombs;
    protected StateMessage.State currentState;
    protected SortedSet<String> playerNames;
    protected int currentPlayers;
    protected int explosionCounter = 0;


    /**
     * Constructor.
     *
     * @param server
     * @param size		Board size, always a square
     */
    public GameBoard(IServer server, ServerBoard b, Config c)
    {
        init(server, b, c);
    }


    /**
     * Initializes the data and hook into the server.
     *
     * @param server
     * @param tiles
     */
    private void init(IServer server, ServerBoard b, Config c)
    {
        this.server = server;
        this.b = b;
        this.playerPositions = new HashMap<Integer, Position>();
        this.bombs = new HashMap<Integer, Position>();
        this.explodingBombs = new HashMap<Integer, Position>();
        this.players = new HashMap<Integer, Player>();
        this.currentState = StateMessage.State.NOTSTARTED;
        this.currentPlayers = 0;
        this.conf = c;

        this.b.setPlayers(playerPositions);
        this.b.setBombs(bombs);

        this.b.addStartingPosition(new Position(5, 5));
        this.b.placePowerup(new Position(10, 10));

        this.playerNames = new TreeSet<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                String[] split1 = o1.split(" ");
                String[] split2 = o2.split(" ");

                return Integer.parseInt(split1[1]) - Integer.parseInt(split2[1]);
            }
        });

        for(int i=0; i<MAX_PLAYERS; i++)
        {
            playerNames.add(String.format("Player %d", i+1));
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
            public void connected(IClient c, String args)
            { synchronized(that) {
                if(currentState == StateMessage.State.END)
                {
                    server.queueMessage(new MetaMessage(Type.REJECT, "Game is over"), c);
                    return;
                }

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
                    logger.log(Level.WARNING, "Invalid connection message: " + args);
                }
            }}
        });

        server.addDisconnectionListener(new DisconnectionListener() {
            @Override
            public void disconnected(IClient c)
            { synchronized(that) {
                removePlayer(c);
            }}
        });

        server.addMessageListener(new MessageListener() {
            @Override
            public void newMessage(IClient c, IMessage m)
            { synchronized(that) {
                if(currentState == StateMessage.State.END)
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
        if(currentState == State.STARTED)
        {
            sendInitialState(c);
        }
        else
        {
            server.queueMessage(new StateMessage(currentState), c);
        }
    }


    /**
     * Adds a player
     *
     * @param c
     */
    private void addPlayer(IClient c, boolean isMonster)
    {
        Position pos;
        if(currentPlayers == Math.min(MAX_PLAYERS, b.maxSupportedPlayers()) && !isMonster)
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
            String name = playerNames.first();
            playerNames.remove(name);
            p = new Player(c.getId(), name);
            currentPlayers++;
            // pos = b.getNextPosition();
        }

        players.put(p.getId(), p);

        // new player position
        pos = b.getNextPosition();

        logger.log(Level.INFO, String.format("%s starts at %s", p.getName(), pos));
        setPlayerPosition(c.getId(), pos);

        if(currentState == StateMessage.State.STARTED)
        {
            // send player the initial state
            sendInitialState(c);

            // notify everyone of new player
            server.queueMessage(new PlayerMessage(c.getId(), pos, p.getType(), p.getName()));
        }
        else
        {
            server.queueMessage(new StateMessage(currentState), c);
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
        server.queueMessage(new StateMessage(currentState), c);

        // send the map
        server.queueMessage(new MapMessage(b.createSendableBoard()), c);

        // send everyone's current positions
        for(Entry<Integer, Position> e: playerPositions.entrySet())
        {
            Position pos = e.getValue();
            Player p = players.get(e.getKey());
            server.queueMessage(new PlayerMessage(e.getKey(), pos, p.getType(), p.getName()), c);
        }

        for(Position p: b.getPowerups())
        {
            server.queueMessage(new PowerupMessage(Action.ADD, p), c);
        }

        synchronized(explodingBombs)
        {
            for(Entry<Integer, Position> e: bombs.entrySet())
            {
                server.queueMessage(new BombMessage(e.getValue(), 0), c);
            }

            for(Entry<Integer, Position> e: explodingBombs.entrySet())
            {
                server.queueMessage(new BombMessage(e.getValue(), conf.defaultExplosionSize), c);
            }
        }
    }


    /**
     * Sends everything we got to everyone.
     */
    private void sendInitialState()
    {
        // send the current state
        server.queueMessage(new StateMessage(currentState));

        // send the map
        server.queueMessage(new MapMessage(b.createSendableBoard()));

        for(Position p: b.getPowerups())
        {
            server.queueMessage(new PowerupMessage(Action.ADD, p));
        }

        // send everyone's current positions
        for(Entry<Integer, Position> e: playerPositions.entrySet())
        {
            Position pos = e.getValue();
            Player p = players.get(e.getKey());
            server.queueMessage(new PlayerMessage(e.getKey(), pos, p.getType(), p.getName()));
        }
    }


    /**
     * Removes a player from the game.
     *
     * @param c
     */
    private void removePlayer(IClient c)
    {
        if(playerPositions.containsKey(c.getId()))
        {
            playerPositions.remove(c.getId());
            Player p = players.remove(c.getId());
            playerNames.add(p.getName());
            server.queueMessage(new PlayerMessage(c.getId(), -1, -1, p.getType(), p.getName()));
            currentPlayers--;
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
            logger.log(Level.INFO, String.format("%s tried to do something, but they are dead.", p.getName()));
            return;
        }

        if(m instanceof MoveMessage && currentState == State.STARTED)
        {
            handleMove(c, (MoveMessage)m);
        }
        else if(m instanceof BombPlacedMessage && currentState == State.STARTED)
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
            logger.log(Level.INFO, String.format("%s tried to move too early", c.getId()));
            return;
        }
        else
        {
            p.setLastMoveTime(new Date());
        }

        Position pos = playerPositions.get(c.getId());
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
                    logger.log(Level.INFO, String.format("%s walked into an explosion.", p.getName()));
                    killPlayer(c.getId());
                    return;
                }

                setPlayerPosition(c.getId(), newPosition);
                server.queueMessage(new PlayerMessage(c.getId(), x, y, p.getType(), p.getName()));

                if(b.getPowerup(newPosition))
                {
                    p.incrementRemainingBombs(); // extra bomb
                    server.queueMessage(new PowerupMessage(Action.REMOVE, newPosition));
                }

                // reveal the exit if it's hidden
                if(b.isExitHidden() && b.isExit(x, y))
                {
                    logger.log(Level.INFO, "Found exit");
                    b.setExitHidden(false);
                    server.queueMessage(new MapMessage(b.createSendableBoard()));
                }
                // end the game if they stepped onto a visible exit
                else if(!b.isExitHidden() && b.isExit(x, y))
                {
                    logger.log(Level.INFO, "Game over");
                    currentState = StateMessage.State.END;
                    server.queueMessage(new StateMessage(currentState));
                }
            }
            else
            {
                logger.log(Level.INFO, String.format(
                        "%s tried to move from %s to (%d,%d), but failed\n",
                        p.getName(), pos, x, y));
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
                        logger.log(Level.INFO, "A monster tried to step on another monster.");
                        return;
                    }
                    else
                    {
                        logger.log(Level.INFO, String.format("%s got killed by a monster.", target.getName()));
                        killPlayer(target_id);
                    }
                }
                catch(RuntimeException e){}

                setPlayerPosition(c.getId(), new Position(x,y));
                server.queueMessage(new PlayerMessage(c.getId(), x, y, p.getType(), p.getName()));
            }
            else
            {
                logger.log(Level.INFO, String.format(
                        "Monster %d tried to move from %s to (%d,%d), but failed",
                        c.getId(),pos, x, y));
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
        Position pos = playerPositions.get(c.getId());
        Player p = players.get(c.getId());

        if(!p.decrementRemainingBombs())
        {
            logger.log(Level.INFO, String.format(
                    "%s tried to place a bomb, but they can't", p.getName()));
            return;
        }

        logger.log(Level.INFO, String.format(
                "%s placed a bomb at %s", p.getName(), pos));

        final Object that = this;
        Bomb b = new Bomb(c.getId(), conf.bombTimer);
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
        if(currentState != StateMessage.State.NOTSTARTED)
        {
            return;
        }

        if(m.getState() == StateMessage.State.STARTED &&
           currentState == StateMessage.State.NOTSTARTED)
        {
            startGame();
        }
    }


    /**
     * Initializes the game.
     */
    protected void startGame()
    {
        currentState = StateMessage.State.STARTED;
        sendInitialState();
    }


    /**
     * Handles the bomb's explosion.
     *
     * @param owner
     * @param bomb
     */
    private void handleBombExplode(int owner, final int bomb)
    { synchronized(explodingBombs) {
        final Position p = bombs.get(bomb);
        int x = p.getX(), y = p.getY();

        explosionCounter++;

        // right
        // includes the square with the bomb itself
        for(int i=0; i<conf.defaultExplosionSize && handleBombExplodeTile(x + i, y); i++);

        // left
        for(int i=1; i<conf.defaultExplosionSize && handleBombExplodeTile(x - i, y); i++);

        // up
        for(int i=1; i<conf.defaultExplosionSize && handleBombExplodeTile(x, y + i); i++);

        // down
        for(int i=1; i<conf.defaultExplosionSize && handleBombExplodeTile(x, y - i); i++);

        bombs.remove(bomb);

        Player player = players.get(owner);

        // player could have disconnected by the time the bomb explodes
        if(player != null)
        {
            player.incrementRemainingBombs();
        }

        if(player == null)
        {
            logger.log(Level.INFO, String.format(
                    "Disconnected player's bomb exploded at %s", p));
        }
        else
        {
            logger.log(Level.INFO, String.format(
                    "%s's bomb exploded at %s", player.getName(), p));
        }

        explodingBombs.put(bomb, p);

        // in case some blocks were destroyed
        // FIXME: should check if anything changed first
        server.queueMessage(new MapMessage(b.createSendableBoard()));
        server.queueMessage(new BombMessage(p, conf.defaultExplosionSize));

        new Thread() {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(conf.explosionDuration);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                    return;
                }

                // delete the bomb after a timeout
                server.queueMessage(new BombMessage(p, -1));

                synchronized(explodingBombs)
                {
                    explodingBombs.remove(bomb);
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
            if(b.isExit(x, y))
            {
                b.setTile(x, y, Tile.EMPTY);
                b.setExitHidden(false);
            }
            else
            {
                b.setTile(x, y, Tile.EMPTY);
            }
        }


        try
        {
            int id = b.playerAt(x, y);
            killPlayer(id);
            // FIXME
            logger.log(Level.INFO, String.format("Player #%d killed by bomb", id));
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
    { synchronized(explodingBombs) {

        for(Position bomb: explodingBombs.values())
        {
            if((bomb.getX() == p.getX() && Math.abs(p.getY() - bomb.getY()) < conf.defaultExplosionSize) ||
               (bomb.getY() == p.getY() && Math.abs(p.getX() - bomb.getX()) < conf.defaultExplosionSize))
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
        playerPositions.put(id, new Position(-1,-1));
        server.queueMessage(new PlayerMessage(id, -1, -1, p.getType(), p.getName()));
    }


    /**
     * Set a player's position.
     *
     * @param p
     * @param pos
     */
    private void setPlayerPosition(int id, Position pos)
    {
        playerPositions.put(id, pos);
    }
}
