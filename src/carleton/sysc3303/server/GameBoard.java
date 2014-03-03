package carleton.sysc3303.server;
/*
 * Game board legend
 *
 * B represents a brick wall, W represents a wooden wall(can be blown up by bombs)
 * E represents the tile is empty, X represents the exit
 * O represents a bomb is presents, A represents an AI is present
 * Any lower case letter represents a player
 *
 * 	Game board representation(n X n)
 *  0n	1n	2n	3n	4n	5n	6n	7n	...	nn
 *  ...	...	...	...	...	...	...	...	...	...
 * 	07	17	27	37	47	57	67	77	...	n7
 * 	06	16	26	36	46	56	66	76	...	n6
 * 	05	15	25	35	45	55	65	75	...	n5
 * 	04	14	24	34	44	54	64	74	...	n4
 * 	03	13	23	33	43	53	63	73	...	n3
 * 	02	12	22	32	42	52	62	72	...	n2
 * 	01	11	21	31	41	51	61	71	...	n1
 * 	00	10	20	30	40	50	60	70	...	n0
 */


import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MetaMessage.Type;
import carleton.sysc3303.server.connection.*;


public class GameBoard
{
    public static final int MAX_PLAYERS = 4;

    private Tile tiles[][];
    private int size;
    private boolean exit_found;
    private IServer server;
    private Map<Player, Position> players;
    private Queue<Character> letters;


    /**
     * Constructor.
     *
     * @param server
     * @param size		Board size, always a square
     */
    public GameBoard(IServer server, int size)
    {
        Tile[][] tmp = new Tile[size][size];
        for(int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
            {
                tmp[i][j] = Tile.EMPTY;
            }
        }

        init(server, tmp);
    }


    /**
     * Alternative constructor.
     *
     * @param server
     * @param tiles
     */
    public GameBoard(IServer server, Tile[][] tiles)
    {
        init(server, tiles);
    }


    /**
     * File constructor.
     *
     * @param server
     * @param map
     * @throws IOException
     */
    public GameBoard(IServer server, File map) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(map));
        String line;
        ArrayList<Tile[]> tmp = new ArrayList<Tile[]>();

        while((line = reader.readLine()) != null)
        {
            line = line.trim();

            if(line.length() == 0)
            {
                continue;
            }

            Tile[] tmp2 = new Tile[line.length()];

            for(int i=0;i < line.length(); i++)
            {
                switch(line.charAt(i))
                {
                case 'B':
                    tmp2[i] = Tile.WALL;
                    break;
                case 'X':
                    tmp2[i] = Tile.EXIT;
                    break;
                default:
                    tmp2[i] = Tile.EMPTY;
                }
            }

            tmp.add(tmp2);
        }

        reader.close();
        init(server, tmp.toArray(new Tile[][]{}));
    }


    /**
     * Initializes the data and hook into the server.
     *
     * @param server
     * @param tiles
     */
    private void init(IServer server, Tile[][] tiles)
    {
        this.size = tiles.length;
        this.server = server;
        this.players = new HashMap<Player, Position>();
        this.letters = new ArrayDeque<Character>();
        this.tiles = tiles;
        this.exit_found = false; // FIXME temporary

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

        Player p = new Player(c.getId(), letters.remove());

        // new player position
        Position pos;// = getNewPosition();
        if(isOccupied(0,0))
        {
            pos = new Position((size-1),(size-1));
        }
            else
            {
                pos = new Position(0,0);
            }

        System.out.printf("Player %d starts at (%d,%d)\n", p.getId(), pos.getX(), pos.getY());
        setPlayerPosition(p, pos);

        // send player the initial state
        sendInitialState(c);

        // notify everyone of new player
        server.pushMessageAll(new PosMessage(p.getId(), pos.getX(), pos.getY()));
    }


    /**
     * Sends everything we got to newly connected clients.
     *
     * @param c
     */
    private void sendInitialState(IClient c)
    {
        // send the map
        server.pushMessage(new MapMessage(getWalls()), c);

        // send everyone's current positions
        for(Entry<Player, Position> e: players.entrySet())
        {
            Player p = e.getKey();
            Position pos = e.getValue();
            server.pushMessage(new PosMessage(p.getId(), pos.getX(), pos.getY()), c);
        }
    }


    /**
     * Returns Tile[][] with walls.
     *
     * @return
     */
    private Tile[][] getWalls()
    {
        Tile[][] walls = new Tile[size][size];

        // get values from map
        for(int i = 0 ; i < size ; i++)
        {
            for(int j = 0; j < size; j++)
            {
                Tile current = getTile(i, j);

                if(current == Tile.EXIT && !exit_found)
                {
                    walls[i][j] = Tile.EMPTY;
                }
                else
                {
                    walls[i][j] = current;
                }
            }
        }

        return walls;
    }


    /**
     * Removes a player from the game.
     *
     * @param c
     */
    private void removePlayer(IClient c)
    {
        Player p = playerFromId(c.getId());

        if(p != null)
        {
            Position pos = players.remove(p);
            setTile(pos.getX(), pos.getY(), Tile.EMPTY);
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
        int randomX;                            //Random int
        int randomY;                            //Random int
        Random randomGenerator = new Random();    //Random generator

        System.out.println("Getting new player pos");
        // Find available start location
        do
        {
            randomX = randomGenerator.nextInt(size);
            randomY = randomGenerator.nextInt(size);
            System.out.println("Trying position at: " + randomX + ", " + randomY);

        } while (isOccupied(randomX, randomY));

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
        Player p = playerFromId(c.getId());
        Position pos = players.get(p);
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

        if(isValidPosition(x, y) && !isOccupied(x, y))
        {
            setPlayerPosition(p, new Position(x, y));
            server.pushMessageAll(new PosMessage(p.getId(), x, y));
            System.out.printf(
                    "Player %d moved from (%d,%d) to (%d,%d)",
                    p.getId(), pos.getX(), pos.getY(), x, y);

            System.out.println(tiles[x][y]);

            if(!exit_found && isExit(x, y))
            {
                System.out.println("Found exit");
                exit_found = true;
                server.pushMessageAll(new MapMessage(getWalls()));
            }
        }
        else
        {
            System.out.printf(
                    "Player %d tried to move from (%d,%d) to (%d,%d), but failed\n",
                    p.getId(), pos.getX(), pos.getY(), x, y);
        }
    }


    /**
     * Set a player's position.
     *
     * @param p
     * @param pos
     */
    private void setPlayerPosition(Player p, Position pos)
    {
        // clear previous position, if any
        if(players.containsKey(p))
        {
            Position oldPos = players.get(p);
            setTile(oldPos.getX(), oldPos.getY(), Tile.EMPTY);
        }

        players.put(p, pos);
    }


    /**
     * Gets a player from their unique id.
     * TODO: make this a constant time method
     *
     * @param i
     * @return
     */
    private Player playerFromId(int i)
    {
        for(Player p: players.keySet())
        {
            if(p.getId() == i)
            {
                return p;
            }
        }

        return null;
    }


    /**
     * Gets tile at a certain position.
     *
     * @param x
     * @param y
     * @return
     */
    private Tile getTile(int x, int y)
    {
        return tiles[x][y];
    }


    /**
     * Sets a tile to a certain value.
     *
     * @param x
     * @param y
     * @param c
     */
    private void setTile(int x, int y, Tile c)
    {
        tiles[x][y] = c;
    }


    /**
     * Generates a random board.
     *
     * @param n
     */
    public void randomBoardGenerator()
    {
        //i and j are used to go through the array
        int i = 0;
        int j = 0;
        int counter = 0;//Counter used to count the # of walls being added to the board
        int randomInt;//Random int to generate the board
        Random randomGenerator = new Random();//Random generator

        //Initialize every position to be empty
        for(i = 0 ; i < size ; i++)
        {
            for(j = 0; j < size; j++)
            {
                setTile(i, j, Tile.EMPTY);
            }
        }

        //Add an exit
        while(true)
        {
            //Assumes an n X n board
            //randomInt = any number between 0 and (n-2) * 11
            //Its n-2 so as to remove the top edge and right edge from consideration
            randomInt = randomGenerator.nextInt((size - 2) * 11);
            j = randomInt % 10;
            i = randomInt % 100;



            //If the position is not an edge
            if(isAnEdge(i,j) == 5)
            {
                if(isValidPosition(i,j))
                {
                    tiles[i][j] = Tile.EXIT;
                    break;
                }
            }
        }


        //Add 2 * n Brick Walls
        while(counter < (2 * size))
        {
            //Assumes n X n board
            //randomInt = any number between 0 and (n-2) * 11
            //Its n-2 so as to remove the top edge and right edge from consideration
            randomInt = randomGenerator.nextInt((size - 2) * 11);
            j = randomInt % 10;
            i = j % 10;

            //If the position is not an edge
            if(isAnEdge(i,j) == 5)
            {
                //Checks if the board is big enough for the given position
                if(isValidPosition(i,j))
                {
                    //Checks if the current position is empty
                    if(tiles[i][j] == Tile.EMPTY)
                    {
                        //Places a wall
                        tiles[i][j] = Tile.WALL;
                        counter++;
                    }
                }
            }
        }

    }


    /**
     * Checks if a position is on a gameboard.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isValidPosition(int x, int y)
    {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    /**
     * Checks if any player is on tile[x][y].
     *
     * @param x
     * @param y
     * @return
     */
    private boolean checkPlayerPos(int x, int y)
    {
        for(Entry<Player, Position> e: players.entrySet())
        {
            Position pos = e.getValue();
            if (pos.getX() == x && pos.getY() == y)
                return false;
        }
        return true;
    }


    /**
     * Checks to see if a tile is occupied.
     * TODO: make this a constant time complexity function.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isOccupied(int x, int y)
    {
        if(isExit(x, y))
        {
            return false;
        }

        Position tmp = new Position(x, y);

        for(Position p: players.values())
        {
            if(p.equals(tmp))
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks if the tile is an exit.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isExit(int x, int y)
    {
        return tiles[x][y] == Tile.EXIT;
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


    /**
     * First checks if player wants to move on the x-axis or the y-axis
     * Once the direction of movement is determined, check for edges
     * If player is at an edge, he can only move in 1 direction (left or right) or (up or down)
     * Once its been determined the player wants to move in a valid direction, checks if that tile is occupied
     * If player passes all the checks he is allowed to move, else move is not valid
     * (x,y) represents the current position of the players
     * (x2,y2) represents the position the player wants to move to
     *
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @return
     */
    public boolean isMoveValid(int x,int y, int x2, int y2)
    {
        int edge = isAnEdge(x,y);

        //Checks if the 2 tiles are adjacent
        if(!isAdjacent(x,y,x2,y2))
        {
            return false;
        }

        //2 different x coordinate but same y coordinates, player is moving left or right
        if(Math.abs(x - x2) == 1)
        {
            if(Math.abs(y - y2) == 0)
            {
                //Checks if current position is an edge

                //Current position is the left edge can only move right
                if(edge % 3 == 1)
                {
                    if(x2 > x)
                    {
                        //Check if position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    else
                        return false;
                }

                //Current position is the right edge can only move left
                if(edge % 3 == 0)
                {
                    if(x2 < x)
                    {
                        //Check if position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    else
                        return false;
                }

                //Current position is not an edge can move left or right
                else
                {
                    if(x2 + 1 == x)
                    {
                        //Check if new position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    if(x2 - 1 == x)
                    {
                        //Check if new position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    else
                        return false;
                }
            }
        }

        //2 different y coordinates but same x coordinates, player is moving up or down
        if(Math.abs(y - y2) == 1)
        {
            if(Math.abs(x - x2) == 0)
            {
                //Check for top and bottom edges

                //Current position is the bottom edge can only move up
                if(edge <= 3)
                {
                    if(y2 > y)
                    {
                    //	Check if new position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    else
                        return false;
                }

                //Current position is the top edge can only move down
                if(edge > 6 && edge < 10)
                {
                    if(y2 < y)
                    {
                        //Check if new position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    else
                        return false;
                }

                //Current position is not an edge can move up or down
                else
                {
                    if(y2 + 1 == y)
                    {
                        //Check if new position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    if(y2 - 1 == y)
                    {
                        //Check if new position is occupied
                        if(!isOccupied(x2,y2))
                            return true;
                    }

                    else
                        return false;
                }
            }
        }

        //Either the difference between the x or y coordinates is bigger then 1, or both x and y coordinates
        //have different values
        return false;
    }

    /**
     * Used to find out if a player is on an edge, and if he is what edge he is on
     *
     * 			Graphic representation of the return value, every value that is not 5 is an edge
     * 			1,3,7 and 9 the corners
     *
     * 			7	-	8	-	9
     * 			|				|
     * 			4		5		6
     * 			|				|
     * 			1	-	2		3
     *
     * @param x
     * @param y
     * @return
     */
    public int isAnEdge(int x, int y)
    {
        if(x == 0 && y == 0)//Bottom left corner
            return 1;
        if(x != 0 && x != size && y == 0)//Bottom edge minus the corners
            return 2;
        if(x == size && y == 0)//Bottom right corner
            return 3;
        if(x == 0 && y != 0 && y != size)//Left edge minus the corners
            return 4;
        if(x != 0 && x != size && y != 0 && y != size)//The middle not an edge
            return 5;
        if(x == size && y != 0 && y != size)//Right edge minus the corners
            return 6;
        if(x == 0 && y == size)//Top left corner
            return 7;
        if(y == size && x !=0 && x != size)//Top edge minus the corners
            return 8;
        if(x == size && y == size)//Top right corner
            return 9;

        return 0;//Else error
    }


    /**
     * Checks if a player found the exit.
     *
     * @param p
     */
    /*public void isExit(Player p)
    {
        int x, y;
        x = p.getPosition().x;
        y = p.getPosition().y;

        if(tiles[x][y] == 'X')
        {
            System.out.println(p.getName() + " has found the exit");
            System.out.println(p.getName() + " has won the game");
            //Signal clients player p as won
            //End the game
        }
    }*/
}
