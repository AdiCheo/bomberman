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
    private int offset_x, offset_y, block_size, draw_size;

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
        draw_size = size * (int)(0.9 * Math.min(getWidth(), getHeight() / size));
        offset_x = (getWidth() - draw_size)/2;
        offset_y = (getHeight() - draw_size)/2;
        block_size = draw_size / size;

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
                drawSquare(g, p);
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
            drawSquare(g, e.getValue());
        }

        if(bombs != null)
        {
            //draw bombs
            for(Entry<Position, Integer> e: bombs.entrySet())
            {
                drawBomb(g, e.getKey(), e.getValue());
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


    /**
     * Simplifies drawing squares.
     *
     * @param g
     * @param x
     * @param y
     */
    private void drawSquare(Graphics2D g, int x, int y)
    {
        g.fillRect(
                offset_x + x * block_size,
                offset_y + draw_size - ((y + 1) * block_size),
                block_size,
                block_size);
    }

    /**
     * Simplifies drawing squares.
     *
     * @param g
     * @param p
     */
    private void drawSquare(Graphics2D g, Position p)
    {
        drawSquare(g, p.getX(), p.getY());
    }


    /**
     * Simplifies drawing bombs.
     *
     * @param g
     * @param x
     * @param y
     */
    private void drawBomb(Graphics2D g, int x, int y, int size)
    {
        if(size > 0)
        {
            g.setColor(Color.ORANGE);

            // right
            for(int i=0; i<size && walls.isPositionValid(x+i, y) && walls.isEmpty(x+i, y); i++)
            {
                drawSquare(g, x+i, y);
            }

            // left
            for(int i=1; i<size && walls.isPositionValid(x-i, y) && walls.isEmpty(x-i, y); i++)
            {
                drawSquare(g, x-i, y);
            }

            // up
            for(int i=1; i<size && walls.isPositionValid(x, y+i) && walls.isEmpty(x, y+i); i++)
            {
                drawSquare(g, x, y+i);
            }

            // down
            for(int i=1; i<size && walls.isPositionValid(x, y-i) && walls.isEmpty(x, y-i); i++)
            {
                drawSquare(g, x, y-i);
            }
        }

        g.setColor(Color.BLACK);
        g.fillOval(
                offset_x + x * block_size,
                offset_y + draw_size - ((y + 1) * block_size),
                block_size,
                block_size);
    }


    /**
     * Simplifies drawing bombs.
     *
     * @param g
     * @param p
     */
    private void drawBomb(Graphics2D g, Position p, int size)
    {
        drawBomb(g, p.getX(), p.getY(), size);
    }
}
