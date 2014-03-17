package carleton.sysc3303.server;

import java.io.*;
import java.util.*;

import carleton.sysc3303.common.*;

/**
 * Game board class with server specific changes.
 *
 * @author Kirill Stepanov
 */
public class ServerBoard extends Board
{
    private boolean exit_hidden;
    private Position exit;
    private List<Position> startingPositions;
    private Set<Position> powerups;


    /**
     * Constructor.
     *
     * @param size
     */
    public ServerBoard(int size)
    {
        super(size);
        this.exit_hidden = true;
        startingPositions = new ArrayList<Position>();
        powerups = new HashSet<Position>();
    }


    /**
     * Adds starting positions from a list.
     *
     * @param positions
     */
    public void setStartingPositions(List<Position> positions)
    {
        startingPositions = new ArrayList<Position>(positions);
    }


    /**
     * Adds a single starting position.
     *
     * @param p
     */
    public void addStartingPosition(Position p)
    {
        startingPositions.add(p);
    }


    /**
     * Unserializing constructor.
     *
     * @param s
     */
    public ServerBoard(String s)
    {
        super(s);
    }


    /**
     * Creates a new board with no exit if it has not been discovered.
     *
     * @return
     */
    public Board createSendableBoard()
    {
        Board b = new Board(serialize());

        if(exit != null && exit_hidden)
        {
            boolean destructable = b.getTile(exit) == Tile.DESTRUCTABLE;
            b.setTile(exit, destructable ? Tile.DESTRUCTABLE : Tile.EMPTY);
        }

        return b;
    }


    /**
     * Creates a board from a file.
     *
     * @param f
     * @throws IOException
     */
    public static ServerBoard fromFile(File f) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        int size = Integer.parseInt(reader.readLine().trim());
        ServerBoard b = new ServerBoard(size);
        String line;
        int pX, pY;
        int y = 0;

        while((line = reader.readLine()) != null)
        {
            if(line.charAt(0) != ':')
            {
                for(int x=0; x<line.length(); x+=2)
                {
                    Tile t;

                    switch(line.charAt(x))
                    {
                    case '#':
                        t = Tile.WALL;
                        break;
                    case '0':
                        t = Tile.EXIT;
                        break;
                    default:
                        t = Tile.EMPTY;
                    }

                    b.setTile(x/2, size - y - 1, t);

                    if(line.charAt(x+1) == '+')
                    {
                        b.setTile(x/2, size - y - 1, Tile.DESTRUCTABLE);
                    }
                }

                y++;
            }
            else
            {
                String[] split = line.substring(1).split(":");

                for(String s: split)
                {
                    String[] split2 = s.split(",");
                    pX = Integer.parseInt(split2[0]);
                    pY = Integer.parseInt(split2[1]);
                    b.addStartingPosition(new Position(pX, pY));
                }
            }
        }

        reader.close();
        return b;
    }


    /**
     * Places a powerup on the board.
     *
     * @param p
     */
    public void placePowerup(Position p)
    {
        powerups.add(p);
    }


    /**
     * Gets list of all powerups.
     *
     * @return
     */
    public List<Position> getPowerups()
    {
        return new ArrayList<Position>(powerups);
    }


    /**
     * Uses up a powerup at the given position.
     *
     * @param p
     * @return
     */
    public boolean getPowerup(Position p)
    {
        return powerups.remove(p);
    }


    /**
     * Checks if the exit was revealed.
     *
     * @return
     */
    public boolean isExitHidden()
    {
        return exit_hidden;
    }


    /**
     * Sets the current state of the exit.
     *
     * @param b
     */
    public void setExitHidden(boolean b)
    {
        exit_hidden = b;
    }


    /**
     * Checks if the given position has an exit.
     *
     * @param p
     */
    public boolean isExit(Position p)
    {
        return isExit(p.getX(), p.getY());
    }


    /**
     * Checks if the given position has an exit.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isExit(int x, int y)
    {
        checkPosition(x, y);

        if(exit == null)
        {
            return false;
        }

        return exit.getX() == x && exit.getY() == y;
    }


    /**
     * Sets a tile.
     *
     * @param x
     * @param y
     * @param t
     */
    public void setTile(int x, int y, Tile t)
    {
        // allow only a single exit
        if(t == Tile.EXIT)
        {
            if(exit != null)
            {
                setTile(exit, Tile.EMPTY);
            }

            exit = new Position(x, y);
        }

        super.setTile(x, y, t);
    }


    /**
     * Gets the current exit position.
     *
     * @return
     */
    public Position getExit()
    {
        return exit;
    }


    /**
     * Get a starting position for a new player.
     * TODO: make it random but smart.
     *
     * @return
     */
    public Position getEmptyPosition()
    {
        int randomX;
        int randomY;
        Random randomGenerator = new Random();

        System.out.println("Getting new player pos");

        // Find available start location
        do
        {
            randomX = randomGenerator.nextInt(getSize());
            randomY = randomGenerator.nextInt(getSize());

        } while (isOccupied(randomX, randomY) || !isEmpty(randomX, randomY));

        return new Position(randomX, randomY);
    }


    /**
     * Gets the next available position.
     * If ran out of predefined ones, generate a random position.
     *
     * @return
     */
    public Position getNextPosition()
    {
        if(startingPositions.isEmpty())
        {
            return getEmptyPosition();
        }
        else
        {
            Position p = startingPositions.remove(0);

            if(isOccupied(p) || !isPositionValid(p) || hasBomb(p) || !isEmpty(p))
            {
                p = getNextPosition();
            }

            return p;
        }
    }


    /**
     * Gets the wall at the given position (no breakable walls).
     *
     * @param x
     * @param y
     */
    protected Tile getWall(int x, int y)
    {
        Tile t = super.getWall(x, y);

        if(exit_hidden && t == Tile.EXIT)
        {
            return Tile.EMPTY;
        }

        return t;
    }
}
