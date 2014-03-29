package carleton.sysc3303.client.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MoveMessage;
import carleton.sysc3303.common.connection.BombPlacedMessage;
import carleton.sysc3303.common.connection.MoveMessage.Direction;


/**
 * Windows through which players can play.
 */
public class PlayerWindow extends Window implements KeyListener
{
    private static final long serialVersionUID = 2809394224013498599L;


    /**
     * Constructor
     *
     * @param ui
     *
     * @throws IOException
     */
    public PlayerWindow(IConnection c) throws IOException
    {
        super(c);
    }



    @Override
    protected void init()
    {
        super.init();
    }


    /**
     * Hook into the connection events.
     */
    protected void hookEvents()
    {
        super.hookEvents();
        addKeyListener(this);
    }


    @Override
    protected void connect()
    {
        c.queueMessage(MetaMessage.connectPlayer(PlayerTypes.PLAYER));
    }


    /**
     * Key pressed event handler.
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e)
    {
        IMessage m;

        logger.info("User pressed: " + e.getKeyCode());

        switch(e.getKeyCode())
        {
        case KeyEvent.VK_UP:
            m = new MoveMessage(Direction.UP);
            break;
        case KeyEvent.VK_DOWN:
            m = new MoveMessage(Direction.DOWN);
            break;
        case KeyEvent.VK_LEFT:
            m = new MoveMessage(Direction.LEFT);
            break;
        case KeyEvent.VK_RIGHT:
            m = new MoveMessage(Direction.RIGHT);
            break;
        case KeyEvent.VK_B:
            m = new BombPlacedMessage();
            break;
        default:
            return;
        }

        c.queueMessage(m);
    }


    @Override
    public void keyReleased(KeyEvent e){}


    @Override
    public void keyTyped(KeyEvent e){}
}
