package carleton.sysc3303.client.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;
import carleton.sysc3303.common.connection.MoveMessage.Direction;


/**
 * Windows through which players can play.
 */
public class PlayerWindow extends Window implements KeyListener,
                                                    IdListener
{
    private static final long serialVersionUID = 2809394224013498599L;
    private KeyPanel keys;
    private Object keyLock;
    private int currentKey = -1;
    private Timer keyTimer;


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

        keyLock = new Object();
        keyTimer = new Timer();

        setSize(400, 600);
        setMinimumSize(getSize());

        try
        {
            keys = new KeyPanel();
        }
        catch (IOException e)
        {
            logger.severe(e.getMessage());
            return;
        }

        ui.add(keys, BorderLayout.SOUTH);

        keyTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            { synchronized(keyLock) {
                IMessage m;

                switch(currentKey)
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
                default:
                    return;
                }

                c.queueMessage(m);
            }}
        }, 0, 100);
    }


    /**
     * Hook into the connection events.
     */
    protected void hookEvents()
    {
        super.hookEvents();
        addKeyListener(keys);
        addKeyListener(this);
        c.addIdListener(this);
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
        logger.fine("User pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
        IMessage m;

        synchronized(keyLock)
        {
            switch(e.getKeyCode())
            {
            case KeyEvent.VK_SPACE:
                m = new BombPlacedMessage();
                break;
            case KeyEvent.VK_ENTER:
                m = new StateMessage(StateMessage.State.STARTED);
                break;
            default:
                currentKey = e.getKeyCode();
                return;
            }
        }

        c.queueMessage(m);
    }


    @Override
    public void keyReleased(KeyEvent e)
    { synchronized(keyLock) {
        if(e.getKeyCode() == currentKey)
        {
            currentKey = -1;
        }
    }}


    @Override
    public void keyTyped(KeyEvent e){}


    @Override
    public void setId(int id)
    {
        board.setId(id);
    }
}
