package carleton.sysc3303.client.gui;

import java.awt.*;
import javax.swing.JPanel;
import java.util.*;
import java.util.Map.Entry;

import carleton.sysc3303.common.*;


/**
 * Represents the actual displayed board.
 *
 * @author Kirill Stepanov
 */
public class Board extends JPanel
{
    private static final long serialVersionUID = 8372907299046333935L;
    private Tile[][] walls;
    Map<Integer,Position> players;

    /**
     * Set the walls using a boolean matrix.
     *
     * @param walls
     */
    public void setWalls(Tile[][] walls)
    {
        this.walls = walls;
    }

    /**
     * Update the player's positions
     *
     * @param m
     */
    public void setPositions(Map<Integer,Position> m)
    {
        players = m;
    }

    /**
     * Repaint the board.
     *
     * @param _g
     */
    public void paint(Graphics _g)
    {
        super.paint(_g);
        int size = walls.length;

        Graphics2D g = (Graphics2D)_g;
        int draw_size = size * (int)(0.9 * Math.min(getWidth(), getHeight() / size));
        int offset_x = (getWidth() - draw_size)/2;
        int offset_y = (getHeight() - draw_size)/2;
        int block_size = draw_size / size;

        // board background color
        g.setColor(Color.WHITE);
        g.fillRect(offset_x, offset_y, draw_size, draw_size);

        // draw the blocks
        for(int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
            {
                if(walls[i][j] != Tile.EMPTY)
                {
                    Color c;

                    switch(walls[i][j])
                    {
                    case DESTRUCTABLE:
                        c = Color.PINK;
                        break;
                    case EXIT:
                        c = Color.BLUE;
                        break;
                    default:
                        c = Color.BLACK;
                    }

                    g.setColor(c);
                    g.fillRect(
                        offset_x + i * block_size,
                        offset_y + draw_size - ((j+1) * block_size),
                        block_size,
                        block_size);
                }
            }
        }

        //draw players
        g.setColor(Color.RED);
        if(players != null)
        {
            for(Entry<Integer, Position> e: players.entrySet())
            {
                g.fillRect(
                offset_x + e.getValue().getX() * block_size,
                offset_y + draw_size - ((e.getValue().getY()+1) * block_size),
                block_size,
                block_size);
            }
        }


        // draw the lines
        g.setColor(Color.GRAY);
        for(int i=0; i<=size; i++)
        {
            g.drawLine(
                offset_x + i * block_size, offset_y,
                offset_x + i * block_size, offset_y + draw_size);

            g.drawLine(
                offset_x, offset_y + i * block_size,
                offset_x + draw_size, offset_y + i * block_size);
        }
    }
}
