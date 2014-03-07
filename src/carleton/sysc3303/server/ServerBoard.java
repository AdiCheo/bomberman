package carleton.sysc3303.server;

import java.io.*;
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


    /**
     * Constructor.
     *
     * @param size
     */
    public ServerBoard(int size)
    {
        super(size);
        this.exit_hidden = true;
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

        if(exit != null)
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
        int y = 0;

        while((line = reader.readLine()) != null)
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

                b.setTile(x/2, y, t);

                if(line.charAt(x+1) == '+')
                {
                    b.setTile(x/2, y, Tile.DESTRUCTABLE);
                }
            }

            y++;
        }

        reader.close();
        return b;
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
        return getTile(x, y) == Tile.EXIT;
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
