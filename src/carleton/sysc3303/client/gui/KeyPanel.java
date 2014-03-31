package carleton.sysc3303.client.gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class KeyPanel extends JPanel implements KeyListener
{
    private static final long serialVersionUID = -114285144009488634L;

    private Button spacebar, up, right, down, left, enter;
    private int moveAmount = 5;


    /**
     * Constructor.
     *
     * @throws IOException
     */
    public KeyPanel() throws IOException
    {
        Image spacebarImg = ImageIO.read(getClass().getResource("/resources/img/spacebar.png"));
        Image enterImg = ImageIO.read(getClass().getResource("/resources/img/enter.png"));
        Image upImg = ImageIO.read(getClass().getResource("/resources/img/up.png"));
        Image rightImg = ImageIO.read(getClass().getResource("/resources/img/right.png"));
        Image downImg = ImageIO.read(getClass().getResource("/resources/img/down.png"));
        Image leftImg = ImageIO.read(getClass().getResource("/resources/img/left.png"));

        spacebar = new Button(spacebarImg);
        enter = new Button(enterImg);
        up = new Button(upImg);
        right = new Button(rightImg);
        down = new Button(downImg);
        left = new Button(leftImg);

        init();
    }


    /**
     * Initializes the gui.
     */
    private void init()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 2;
        c.gridheight = 1;
        c.gridwidth = 4;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.VERTICAL;
        c.anchor = GridBagConstraints.NORTH;
        add(spacebar, c);

        c.gridx = 4;
        c.gridy = 0;
        c.gridheight = 2;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        add(enter, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.VERTICAL;
        add(up, c);

        c.gridx = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(right, c);

        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        add(left, c);

        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        add(down, c);
    }


    @Override
    public void keyTyped(KeyEvent e)
    {
        // TODO Auto-generated method stub
    }


    @Override
    public void keyPressed(KeyEvent e)
    {
        try
        {
            getButton(e.getKeyCode()).press();
        }
        catch(NoSuchFieldException ex) {}
    }


    @Override
    public void keyReleased(KeyEvent e)
    {
        try
        {
            getButton(e.getKeyCode()).release();
        }
        catch(NoSuchFieldException ex) {}
    }


    private Button getButton(int keycode) throws NoSuchFieldException
    {
        switch(keycode)
        {
        case KeyEvent.VK_DOWN:
            return down;
        case KeyEvent.VK_UP:
            return up;
        case KeyEvent.VK_RIGHT:
            return right;
        case KeyEvent.VK_LEFT:
            return left;
        case KeyEvent.VK_SPACE:
            return spacebar;
        case KeyEvent.VK_ENTER:
            return enter;
        default:
            throw new NoSuchFieldException();
        }
    }


    @SuppressWarnings("serial")
    private class Button extends JButton
    {
        public Button(Image img)
        {
            super(new ImageIcon(img));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusable(false);
            release();
        }


        public void release()
        {
            setMargin(new Insets(0, 0, moveAmount, 0));
        }


        public void press()
        {
            setMargin(new Insets(moveAmount, 0, 0, 0));
        }
    }
}
