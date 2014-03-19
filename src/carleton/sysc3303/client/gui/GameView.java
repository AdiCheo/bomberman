package carleton.sysc3303.client.gui;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;

import carleton.sysc3303.common.*;

public class GameView extends JPanel
{
    private static final long serialVersionUID = -823641346290407577L;
    private DisplayBoard board;
    //private PlayerStatusPanel panel;

    /**
     * Constructor.
     *
     * @throws IOException
     */
    public GameView(DisplayBoard board) throws IOException
    {
        this.board = board;

        init();
    }


    /**
     * UI initialization.
     *
     * @throws IOException
     */
    private void init() throws IOException
    {
        this.setLayout(new BorderLayout());

        add(board, BorderLayout.CENTER);

        // commenting out until the API stabilizes
        /*panel = new PlayerStatusPanel();
        panel.setPreferredSize(new Dimension(50,50));
        panel.setMinimumSize(new Dimension(50,50));
        add(panel, BorderLayout.EAST);

        setMap(new Board(0));*/
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
        //panel.setWalls(b);
    }
}
