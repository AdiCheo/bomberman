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


    /**
     * Constructor.
     *
     * @param size
     */
    public Board(int size)
    {
        this.size = size;
        this.walls = new Tile[size][size];
        this.breakableWalls = new boolean[size][size];
        this.players = new HashMap<Integer, Position>();
    }


    /**
     * Creates a board from a serialized string.
     *
     * @param s
     * @return
     */
    public static Board fromString(String s)
    {
        return new Board(2);
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

        switch(walls[x][y])
        {
        case EMPTY:
        case EXIT:
            return breakableWalls[x][y] && isOccupied(x, y);
        default:
            return false;
        }
    }


    /**
     * Checks if a player exists at the given position.
     *
     * @param p
     * @return
     */
    public boolean isOccupied(Position p)
    {
        checkPosition(p.getX(), p.getY());

        for(Position c: players.values())
        {
            if(p.equals(c))
            {
                return true;
            }
        }

        return false;
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

        if(breakableWalls[x][y])
        {
            return Tile.DESTRUCTABLE;
        }

        return walls[x][y];
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
            breakableWalls[x][y] = true;
        }
        else
        {
            breakableWalls[x][y] = false;
            walls[x][y] = t;
        }
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
     * Converts the map to string.
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

            tmp[pt.getX()][pt.getY()] = c;
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
     * Throws an exception if the position is not valid.
     *
     * @param x
     * @param y
     */
    private void checkPosition(int x, int y)
    {
        if(x < 0 || y < 0 || x >= size || y >= size)
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
    public class PositionTile
    {
        private Position p;
        private Tile t;


        /**
         * Constructor.
         *
         * @param p
         * @param t
         */
        public PositionTile(Position p, Tile t)
        {
            this.p = p;
            this.t = t;
        }


        /**
         * Gets the current position.
         *
         * @return
         */
        public Position getPosition()
        {
            return p;
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


        /**
         * Gets the x-coordinate.
         *
         * @return
         */
        public int getX()
        {
            return p.getX();
        }


        /**
         * Gets the y-coordinate.
         *
         * @return
         */
        public int getY()
        {
            return getY();
        }
    }
}
