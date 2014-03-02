package carleton.sysc3303.client.gui;

import java.awt.*;
import javax.swing.JPanel;


/**
 * Represents the actual displayed board.
 *
 * @author Kirill Stepanov
 */
public class Board extends JPanel
{
    private static final long serialVersionUID = 8372907299046333935L;
    private boolean[][] walls;


    /**
     * Set the walls using a boolean matrix.
     *
     * @param walls
     */
    public void setWalls(boolean[][] walls)
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
        g.setColor(Color.BLACK);
        for(int i=0; i<size; i++)
        {
            for(int j=0; j<size; j++)
            {
                if(walls[i][j])
                {
                    g.fillRect(
                        offset_x + i * block_size,
                        offset_y + j * block_size,
                        block_size,
                        block_size);
                }
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
