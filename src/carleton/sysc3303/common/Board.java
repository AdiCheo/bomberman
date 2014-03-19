package carleton.sysc3303.common;

import java.util.*;
import carleton.sysc3303.common.Board.PositionTile;


/**
 * Generic representation of the game board state.
 *
 * @author Kirill Stepanov.
 */
public class Board implements Iterable<PositionTile>
{
    protected Tile[][] walls;
    protected boolean[][] breakableWalls;
    protected int size;
    protected Map<Integer, Position> players;
    protected Map<Integer, Position> bombs;
    protected Map<Integer, Position> explosions;

    /**
     * Constructor.
     *
     * @param size
     */
    public Board(int size)
    {
        init(size);
    }


    /**
     * Creates a board from a serialized string.
     * The string does not include players.
     *
     * @param s
     * @return
     */
    public Board(String s)
    {
        String[] rows = s.split("\\|");
        init(rows.length);
        Tile[] tiles = Tile.values();

        for(int i=0; i<size; i++)
        {
            for(int j=0; j<size*2; j+=2)
            {
                int t = Integer.parseInt(""+rows[i].charAt(j));
                setTile(j/2, i, tiles[t]);

                if(rows[i].charAt(j+1) == '+')
                {
                    setTile(j/2, i, Tile.DESTRUCTABLE);
                }
            }
        }
    }


    /**
     * Called from constructors only.
     */
    protected void init(int size)
    {
        if(size < 0)
        {
            throw new IllegalArgumentException("Board size may not be negative");
        }

        this.size = size;
        this.walls = new Tile[size][size];
        this.breakableWalls = new boolean[size][size];
        this.players = new HashMap<Integer, Position>();
        this.bombs = new HashMap<Integer, Position>();
        this.explosions = new HashMap<Integer, Position>();

        for(Position p: this)
        {
            walls[p.getX()][p.getY()] = Tile.EMPTY;
            breakableWalls[p.getX()][p.getY()] = false;
        }
    }


    /**
     * Converts the current state into a simple string.
     * Does not include players.
     *
     * @return
     */
    public String serialize()
    {
        StringBuilder sb = new StringBuilder();

        for(PositionTile pt: this)
        {
            if(pt.getX() == 0 && pt.getY() != 0)
            {
                sb.append('|');
            }

            sb.append(getWall(pt).ordinal());
            sb.append(hasBreakableWall(pt) ? '+' : '-');
        }

        return sb.toString();
    }


    /**
     * Gets the board size.
     *
     * @return
     */
    public int getSize()
    {
        return size;
    }


    /**
     * Sets the internal player map.
     *
     * @param m
     */
    public void setPlayers(Map<Integer, Position> players)
    {
        this.players = players;
    }


    /**
     * Sets the bombs.
     *
     * @param bombs
     */
    public void setBombs(Map<Integer, Position> bombs)
    {
        this.bombs = bombs;
    }

    /**
     * Gets current map of players.
     *
     * @return
     */
    public Map<Integer, Position> getPlayers()
    {
        return players;
    }


    /**
     * Gets the bombs.
     *
     * @return
     */
    public Map<Integer, Position> getBombs()
    {
        return bombs;
    }


    /**
     * Checks is position is empty.
     *
     * @param p
     * @return
     */
    public boolean isEmpty(Position p)
    {
        return isEmpty(p.getX(), p.getY());
    }


    /**
     * Checks is position is empty.
     * Includes check for players.
     *
     * @param p
     * @return
     */
    public boolean isEmpty(int x, int y)
    {
        checkPosition(x, y);

        switch(getWall(x, y))
        {
        case EMPTY:
        case EXIT:
            return !hasBreakableWall(x, y);
        default:
            return false;
        }
    }


    /**
     * Gets player id of the one at the given position.
     *
     * @param p
     * @return
     * @throws RuntimeException
     */
    public int playerAt(Position p) throws RuntimeException
    {
        checkPosition(p.getX(), p.getY());

        for(Map.Entry<Integer, Position> e: players.entrySet())
        {
            if(p.equals(e.getValue()))
            {
                return e.getKey();
            }
        }

        throw new RuntimeException(String.format("No player at %s", p));
    }


    /**
     * Gets player id of the one at the given position.
     *
     * @param x
     * @param y
     * @return
     * @throws RuntimeException
     */
    public int playerAt(int x, int y) throws RuntimeException
    {
        return playerAt(new Position(x, y));
    }


    /**
     * Checks if a player exists at the given position.
     *
     * @param p
     * @return
     */
    public boolean isOccupied(Position p)
    {
        try
        {
            playerAt(p); // throws exception if no one present
        }
        catch(RuntimeException e)
        {
            return false;
        }

        return true;
    }


    /**
     * Checks if a player exists at the given position.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isOccupied(int x, int y)
    {
        return isOccupied(new Position(x, y));
    }


    /**
     * Checks if there is a bomb at the given position.
     *
     * @param p
     */
    public boolean hasBomb(Position p)
    {
        checkPosition(p.getX(), p.getY());

        for(Position b: bombs.values())
        {
            if(b.equals(p))
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks if there is a bomb at the given position.
     *
     * @param x
     * @param y
     */
    public boolean hasBomb(int x, int y)
    {
        return hasBomb(new Position(x, y));
    }

    /**
     * Gets the tile at the given position.
     *
     * @param p
     * @return
     */
    public Tile getTile(Position p)
    {
        return getTile(p.getX(), p.getY());
    }


    /**
     * Gets the tile at the given position.
     *
     * @param x
     * @param y
     * @return
     */
    public Tile getTile(int x, int y)
    {
        checkPosition(x, y);

        if(hasBreakableWall(x, y))
        {
            return Tile.DESTRUCTABLE;
        }

        return getWall(x, y);
    }


    /**
     * Sets a tile.
     *
     * @param p
     * @param t
     */
    public void setTile(Position p, Tile t)
    {
        setTile(p.getX(), p.getY(), t);
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
        checkPosition(x, y);

        if(t == Tile.DESTRUCTABLE)
        {
            if(walls[size - y - 1][x] == Tile.WALL)
            {
                throw new IllegalArgumentException(
                        "Cannot have a breakable wall on top of a regular wall.");
            }

            breakableWalls[size - y - 1][x] = true;
        }
        else
        {
            breakableWalls[size - y - 1][x] = false;
            walls[size - y - 1][x] = t;
        }
    }


    /**
     * Checks if the given position is valid.
     *
     * @param p
     * @return
     */
    public boolean isPositionValid(Position p)
    {
        return isPositionValid(p.getX(), p.getY());
    }


    /**
     * Checks if the given position is valid.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isPositionValid(int x, int y)
    {
        return !(x < 0 || y < 0 || x >= size || y >= size);
    }


    /**
     * Creates an iterator for the board.
     *
     * @return
     */
    public Iterator<PositionTile> iterator()
    {
        return new BoardIterator(this);
    }


    /**
     * Converts the map to a human readable string.
     */
    public String toString()
    {
        char[][] tmp = new char[size][size];

        for(PositionTile pt: this)
        {
            char c;

            switch(pt.getTile())
            {
            case EXIT:
                c = '0';
                break;
            case DESTRUCTABLE:
                c = '.';
                break;
            case WALL:
                c = '#';
                break;
            default:
                c = ' ';
            }

            tmp[size - pt.getY() - 1][pt.getX()] = c;
        }

        StringBuilder sb = new StringBuilder();

        for(int i=0; i<size; i++)
        {
            sb.append(tmp[i]);
            sb.append('\n');
        }

        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }


    /**
     * Gets the wall at the given position (no breakable walls).
     *
     * @param p
     * @return
     */
    protected Tile getWall(Position p)
    {
        return getWall(p.getX(), p.getY());
    }


    /**
     * Gets the wall at the given position (no breakable walls).
     *
     * @param x
     * @param y
     * @return
     */
    protected Tile getWall(int x, int y)
    {
        checkPosition(x, y);
        return walls[size - y - 1][x];
    }


    /**
     * Checks if there is a breakable wall at the given position.
     *
     * @param p
     * @return
     */
    protected boolean hasBreakableWall(Position p)
    {
        return hasBreakableWall(p.getX(), p.getY());
    }


    /**
     * Checks if there is a breakable wall at the given position.
     *
     * @param x
     * @param y
     * @return
     */
    protected boolean hasBreakableWall(int x, int y)
    {
        checkPosition(x, y);
        return breakableWalls[size - y - 1][x];
    }


    /**
     * Throws an exception if the position is not valid.
     *
     * @param x
     * @param y
     */
    protected void checkPosition(int x, int y)
    {
        if(!isPositionValid(x, y))
        {
            throw new IllegalArgumentException("At least one value is out of bounds.");
        }
    }


    /**
     * Used for iterating over the board.
     * No need for nested loops.
     */
    public class BoardIterator implements Iterator<PositionTile>
    {
        private Board b;
        private int x, y;

        /**
         * Constructor.
         *
         * @param b
         */
        public BoardIterator(Board b)
        {
            this.b = b;
            this.x = 0;
            this.y = 0;
        }


        @Override
        public boolean hasNext()
        {
            return y != size;
        }


        @Override
        public PositionTile next()
        {
            if(!hasNext())
            {
                return null;
            }

            Position p = new Position(x, y);
            PositionTile pt = new PositionTile(p, b.getTile(p));

            if(x == size - 1)
            {
                x = 0;
                y++;
            }
            else
            {
                x++;
            }

            return pt;
        }


        @Override
        public void remove()
        {
            throw new UnsupportedOperationException(
                    "Cannot modify the board through the iterator.");
        }
    }


    /**
     * The class returned by the iterator.
     */
    public class PositionTile extends Position
    {
        private Tile t;


        /**
         * Constructor.
         *
         * @param p
         * @param t
         */
        public PositionTile(Position p, Tile t)
        {
            super(p.getX(), p.getY());
            this.t = t;
        }


        /**
         * Gets the current tile.
         *
         * @return
         */
        public Tile getTile()
        {
            return t;
        }
    }
}
