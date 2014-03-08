package carleton.sysc3303.client.gui;

import java.awt.*;
import javax.swing.JPanel;
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

        //draw players
        g.setColor(Color.RED);
        for(Entry<Integer, Position> e: walls.getPlayers().entrySet())
        {
            g.fillRect(
            offset_x + e.getValue().getX() * block_size,
            offset_y + draw_size - ((e.getValue().getY()+1) * block_size),
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
