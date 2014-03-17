package carleton.sysc3303.client.gui;

import javax.swing.*;

import java.awt.*;
import java.util.Map;
import java.util.Map.Entry;

import carleton.sysc3303.common.*;

public class GameView extends JPanel
{
    private static final long serialVersionUID = -823641346290407577L;
    private DisplayBoard board;
    private PlayerStatusPanel panel;

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
        
        panel = new PlayerStatusPanel();
        panel.setPreferredSize(new Dimension(50,50));
        panel.setMinimumSize(new Dimension(50,50));
        add(panel, BorderLayout.EAST);

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
        panel.setWalls(b);
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
}
/*
class PlayerStatusPanel extends JPanel
{
	private static final long serialVersionUID = -8814546067076341951L;
	
	public void paint(Graphics _g)
	{
		super.paint(_g);
        int size = 

        Graphics2D g = (Graphics2D)_g;
        draw_size = size * (int)(0.9 * Math.min(getWidth(), getHeight() / size));
        offset_x = (getWidth() - draw_size)/2;
        offset_y = (getHeight() - draw_size)/2;
        block_size = draw_size / size;
        		
        }
	}
}
*/