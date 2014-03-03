package carleton.sysc3303.client.gui;

import javax.swing.*;

import carleton.sysc3303.common.Position;

import java.awt.*;
import java.util.*;

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

        setMap(new boolean[][] {{false}});
    }
    
    /**
     * Lets the board know when changes are made
     * @param id
     * @param m
     */
    public void setPositions(int id, Map<Integer,Position> m)
    {
    	board.setPositions(id, m);
    }


    /**
     * Set the internal board's walls.
     * This should probably called when an event comes
     * in from the connection.
     *
     * @param walls
     */
    public void setMap(boolean[][] walls)
    {
        board.setWalls(walls);
        board.repaint();
    }
}
