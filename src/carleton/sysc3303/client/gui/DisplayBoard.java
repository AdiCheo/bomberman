package carleton.sysc3303.client.gui;

import java.awt.*;
import javax.swing.JPanel;

import java.util.Map;
import java.util.Map.Entry;

import carleton.sysc3303.common.*;
import carleton.sysc3303.common.Board.PositionTile;


/**
 * Represents the actual displayed board.
 *
 * @author Kirill Stepanov
 */
public class DisplayBoard extends JPanel
{
    private static final long serialVersionUID = 8372907299046333935L;
    private Board walls;
    private Map<Integer, Color> colors;
    private Map<Position, Integer> bombs;

    /**
     * Set the walls using a boolean matrix.
     *
     * @param walls
     */
    public void setWalls(Board walls)
    {
        this.walls = walls;
    }


    /**
     * Stores the colors map.
     *
     * @param colors
     */
    public void setColors(Map<Integer, Color> colors)
    {
        this.colors = colors;
    }


    /**
     * Sets the bombs.
     *
     * @param bombs
     */
    public void setBombs(Map<Position, Integer> bombs)
    {
        this.bombs = bombs;
    }


    /**
     * Repaint the board.
     *
     * @param _g
     */
    public void paint(Graphics _g)
    {
        super.paint(_g);
        int size = walls.getSize();

        Graphics2D g = (Graphics2D)_g;
        int draw_size = size * (int)(0.9 * Math.min(getWidth(), getHeight() / size));
        int offset_x = (getWidth() - draw_size)/2;
        int offset_y = (getHeight() - draw_size)/2;
        int block_size = draw_size / size;

        // board background color
        g.setColor(Color.WHITE);
        g.fillRect(offset_x, offset_y, draw_size, draw_size);

        // draw the blocks
        for(PositionTile p: walls)
        {
            if(p.getTile() != Tile.EMPTY)
            {
                Color c;

                switch(p.getTile())
                {
                case DESTRUCTABLE:
                    c = Color.YELLOW;
                    break;
                case EXIT:
                    c = Color.BLUE;
                    break;
                default:
                    c = Color.BLACK;
                }

                g.setColor(c);
                g.fillRect(
                    offset_x + p.getX() * block_size,
                    offset_y + draw_size - ((p.getY() + 1) * block_size),
                    block_size,
                    block_size);
            }
        }

        Color c;

        //draw players
        for(Entry<Integer, Position> e: walls.getPlayers().entrySet())
        {
            if(colors != null && colors.containsKey(e.getKey()))
            {
                c = colors.get(e.getKey());
            }
            else
            {
                c = Color.RED;
            }

            g.setColor(c);
            g.fillRect(
                offset_x + e.getValue().getX() * block_size,
                offset_y + draw_size - ((e.getValue().getY()+1) * block_size),
                block_size,
                block_size);
        }

        if(bombs != null)
        {
            //draw bombs
            c = Color.BLACK;
            for(Entry<Position, Integer> e: bombs.entrySet())
            {
                g.setColor(c);
                g.fillOval(
                        offset_x + e.getKey().getX() * block_size,
                        offset_y + draw_size - ((e.getKey().getY()+1) * block_size),
                        block_size, block_size);
            }
        }
        
        //Draw explosion
        c = Color.ORANGE;
        for(Entry<Integer, Position> e: walls.getExplosion().entrySet())
        {
        	g.setColor(c);
        	g.fillRect(
        			offset_x + e.getValue().getX() * block_size,
        			offset_y + e.getValue().getY() * block_size,
        			block_size,
        			block_size);
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
