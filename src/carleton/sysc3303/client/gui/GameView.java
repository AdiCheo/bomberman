package carleton.sysc3303.client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.*;

import carleton.sysc3303.common.*;

public class GameView extends JPanel
{
    private static final long serialVersionUID = -823641346290407577L;
    private Board board;

    /**
     * Constructor.
     */
    public GameView()
    {
        init();
    }


    /**
     * UI initialization.
     */
    private void init()
    {
        this.setLayout(new BorderLayout());

        board = new Board();
        add(board, BorderLayout.CENTER);

        setMap(new Tile[][] {{Tile.EMPTY}});
    }

    /**
     * Lets the board know when changes are made
     *
     * @param m
     */
    public void setPositions(Map<Integer,Position> m)
    {
        board.setPositions(m);
    }


    /**
     * Set the internal board's walls.
     * This should probably called when an event comes
     * in from the connection.
     *
     * @param walls
     */
    public void setMap(Tile[][] walls)
    {
        board.setWalls(walls);
        board.repaint();
    }
}
