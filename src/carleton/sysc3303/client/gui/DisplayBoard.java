package carleton.sysc3303.client.gui;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.util.*;
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
    //private Map<Integer, Color> colors;
    private Map<Position, Integer> bombs;
    private Set<Position> powerups;
    private int offset_x, offset_y, block_size, draw_size;

    private BufferedImage imgmap;
    private Image tile, destructable, wall, bomb, player, candy,
                exp_b, exp_v, exp_h, exp_vt, exp_vb, exp_hl, exp_hr;
    private int imgUnit;


    /**
     * Constructor.
     *
     * @throws IOException
     */
    public DisplayBoard() throws IOException
    {
        // https://stackoverflow.com/a/14097632
        Image rawImgmap = ImageIO.read(new File("resources/img/img_map.png"));
        ImageFilter filter = new TransparencyFilter(new Color(255, 0, 255));
        ImageProducer filtered = new FilteredImageSource(rawImgmap.getSource(), filter);
        imgmap = toBufferedImage(Toolkit.getDefaultToolkit().createImage(filtered));
        imgUnit = 16;

        candy = ImageIO.read(new File("resources/img/candy.png"));

        init();
    }


    /**
     * Initializes the images.
     */
    private void init()
    {
        tile = cropImgMap(1, 0);
        destructable = cropImgMap(10, 5);
        wall = cropImgMap(11, 4);
        bomb = cropImgMap(12, 2);
        player = cropImgMap(1, 4);

        exp_b = cropImgMap(2, 5);
        exp_v = cropImgMap(15, 1);
        exp_h = cropImgMap(1, 5);
        exp_vt = cropImgMap(15, 0);
        exp_vb = cropImgMap(15, 2);
        exp_hl = cropImgMap(0, 5);
        exp_hr = cropImgMap(3, 5);
    }


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
        //this.colors = colors;
    }


    /**
     * Sets the powerups.
     *
     * @param powerups
     */
    public void setPowerups(Set<Position> powerups)
    {
        this.powerups = powerups;
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

        // draw the blocks
        for(PositionTile p: walls)
        {
            Image img;

            switch(p.getTile())
            {
            case DESTRUCTABLE:
                img = destructable;
                break;
            case EMPTY:
                img = tile;
                break;
            case EXIT:
            default:
                img = wall;
            }

            drawImage(g, img, p);
        }

        // draw powerups
        for(Position p: powerups)
        {
            drawImage(g, candy, p);
        }

        //draw players
        for(Entry<Integer, Position> e: walls.getPlayers().entrySet())
        {
            drawImage(g, player, e.getValue());
        }

        if(bombs != null)
        {
            //draw bombs
            for(Entry<Position, Integer> e: bombs.entrySet())
            {
                drawBomb(g, e.getKey(), e.getValue());
            }
        }
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
            // right
            for(int i=1; i<size && walls.isPositionValid(x+i, y) && walls.isEmpty(x+i, y); i++)
            {
                drawImage(g, exp_h, x+i-1, y);
                drawImage(g, exp_hr, x+i, y);
            }

            // left
            for(int i=1; i<size && walls.isPositionValid(x-i, y) && walls.isEmpty(x-i, y); i++)
            {
                drawImage(g, exp_h, x-i+1, y);
                drawImage(g, exp_hl, x-i, y);
            }

            // up
            for(int i=1; i<size && walls.isPositionValid(x, y+i) && walls.isEmpty(x, y+i); i++)
            {
                drawImage(g, exp_v, x, y+i-1);
                drawImage(g, exp_vt, x, y+i);
            }

            // down
            for(int i=1; i<size && walls.isPositionValid(x, y-i) && walls.isEmpty(x, y-i); i++)
            {
                drawImage(g, exp_v, x, y-i+1);
                drawImage(g, exp_vb, x, y-i);
            }
        }

        drawImage(g, size > 0 ? exp_b : bomb, x, y);
    }


    /**
     * Converts logical board position to starting position in java.
     *
     * @param p
     * @return
     */
    @SuppressWarnings("unused")
    private Position convertCoordinates(Position p)
    {
        return convertCoordinates(p.getX(), p.getY());
    }


    /**
     * Converts logical board position to starting position in java.
     *
     * @param x
     * @param y
     * @return
     */
    private Position convertCoordinates(int x, int y)
    {
        return new Position(
                offset_x + x * block_size,
                offset_y + draw_size - ((y + 1) * block_size));
    }


    /**
     * Draws an image at a given points.
     *
     * @param g
     * @param im
     * @param p
     */
    private void drawImage(Graphics2D g, Image im, Position p)
    {
        drawImage(g, im, p.getX(), p.getY());
    }


    /**
     * Draws an image at a given points.
     *
     * @param g
     * @param im
     * @param p
     */
    private void drawImage(Graphics2D g, Image im, int x, int y)
    {
        Position p = convertCoordinates(x, y);
        g.drawImage(im, p.getX(), p.getY(), block_size, block_size, null);
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


    /**
     * Crops the image map and gets one square slice.
     *
     * @param x
     * @param y
     * @return
     */
    private Image cropImgMap(int x, int y)
    {
        return imgmap.getSubimage(x * imgUnit, y * imgUnit, imgUnit, imgUnit);
    }


    /**
     * Converts a given Image into a BufferedImage
     *
     * https://stackoverflow.com/a/13605411
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    private static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
