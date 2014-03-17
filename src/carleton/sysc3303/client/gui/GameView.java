package carleton.sysc3303.client.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        setPowerups(new HashSet<Position>());
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
    }


    /**
     * Sets the color mappings.
     *
     * @param colors
     */
    public void setColors(Map<Integer, Color> colors)
    {
        board.setColors(colors);
    }


    /**
     * Sets the bombs.
     *
     * @param bombs
     */
    public void setBombs(Map<Position, Integer> bombs)
    {
        board.setBombs(bombs);
    }


    /**
     * Sets the powerups.
     *
     * @param powerups
     */
    public void setPowerups(Set<Position> powerups)
    {
        board.setPowerups(powerups);
    }
}
