package carleton.sysc3303.client.gui;

import javax.swing.*;
import java.awt.*;

import carleton.sysc3303.common.*;

public class GameView extends JPanel
{
    private static final long serialVersionUID = -823641346290407577L;
    private DisplayBoard board;

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

        board = new DisplayBoard();
        add(board, BorderLayout.CENTER);

        setMap(new Board(0));
    }


    /**
     * Set the internal board's walls.
     * This should probably called when an event comes
     * in from the connection.
     *
     * @param walls
     */
    public void setMap(Board b)
    {
        board.setWalls(b);
        board.repaint();
    }
}
