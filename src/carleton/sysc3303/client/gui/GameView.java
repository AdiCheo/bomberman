package carleton.sysc3303.client.gui;

import javax.swing.*;
import java.awt.*;

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
