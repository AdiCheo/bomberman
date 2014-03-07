package carleton.sysc3303.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import carleton.sysc3303.common.*;

/**
 * Game board class with server specific changes.
 *
 * @author Kirill Stepanov
 */
public class ServerBoard extends Board
{
    private boolean exit_hidden;


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
     * Creates a board from a file.
     *
     * @param f
     * @throws IOException
     */
    public ServerBoard fromFile(File f) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null)
        {
            sb.append(line);
            sb.append('|');
        }

        reader.close();

        sb.deleteCharAt(sb.length() - 1);
        return new ServerBoard(sb.toString());
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
