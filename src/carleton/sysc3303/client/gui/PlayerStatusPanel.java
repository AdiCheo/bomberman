package carleton.sysc3303.client.gui;

import java.awt.*;

import javax.swing.JPanel;

import java.util.Map;
import java.util.Map.Entry;

import carleton.sysc3303.common.*;
import carleton.sysc3303.common.Board.PositionTile;

public class PlayerStatusPanel extends JPanel
{
    private static final long serialVersionUID = 8372907299046333935L;
    private Board walls;    private int offset_x, offset_y, block_size, draw_size;
	private Map<Integer, Position> players;
	
	public void setWalls(Board walls)
	{
		this.walls = walls;
	}
	
	public void setPlayers(Map<Integer, Position> players)
	{
		this.players = players;
	}
	
	public void paint(Graphics _g)
    {
        super.paint(_g);
        int size = walls.getSize();

        Graphics2D g = (Graphics2D)_g;
        draw_size = size * (int)(0.9 * Math.min(getWidth(), getHeight() / size));
        offset_x = (getWidth() - draw_size)/2;
        offset_y = (getHeight() - draw_size)/2;
        block_size = draw_size / size;
        
        g.setColor(Color.WHITE);
        g.fillRect(offset_x, offset_y, draw_size, draw_size);
        
        // display player info
        g.setColor(Color.BLACK);
        int i = 0;
        for(Entry<Integer, Position> e: walls.getPlayers().entrySet())
        {
        	g.drawString("Player " + e.getKey() + " is at location " + e.getValue(),
        			offset_x, offset_y + (i * 20));
        	Position pos = e.getValue();
        	if(walls.isPositionValid(pos))
        		g.drawString("Player " + e.getKey() + " is dead",
        				offset_x, offset_y + (i * 30));
        		else
        			g.drawString("Player " + e.getKey() + " is alive",
        					offset_x, offset_y  + (i * 40));
        	
        	i++;
        }
	}

}
