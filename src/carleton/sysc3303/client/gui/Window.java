package carleton.sysc3303.client.gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.*;
import carleton.sysc3303.common.connection.*;

/**
 * The primary display window.
 *
 * @author Kirill Stepanov
 */
public class Window extends JFrame implements MessageListener,
                                              ConnectionStatusListener,
                                              GameStateListener
{
    public enum States { LOADING, GAME, DONE };

    private static final long serialVersionUID = 7088369983891361413L;
    private GameView ui;
    private CardLayout layout;
    private JPanel loading_panel;
    private JLabel loading_label;
    private Set<Position> powerups;
    private IConnection c;
    private Map<Integer, Position> positions;
    private Map<Integer, Color> colors;
    private Map<Position, Integer> bombs;


    /**
     * Constructor
     *
     * @param ui
     *
     * @throws IOException
     */
    public Window(IConnection c) throws IOException
    {
        this.c = c;
        this.ui = new GameView();
        positions = new ConcurrentHashMap<Integer, Position>();
        colors = new ConcurrentHashMap<Integer, Color>();
        bombs = new ConcurrentHashMap<Position, Integer>();
        powerups = new HashSet<Position>();

        init();
        hookEvents();
    }


    /**
     * Initialize the GUI.
     */
    private void init()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setMinimumSize(getSize());
        setTitle("Bomberman");

        layout = new CardLayout();
        setLayout(layout);

        loading_panel = new JPanel();
        loading_label = new JLabel("Loading");
        loading_panel.add(loading_label);

        JPanel done_panel = new JPanel();
        done_panel.add(new JLabel("Game over"));

        add(loading_panel, States.LOADING.toString());
        add(ui, States.GAME.toString());
        add(done_panel, States.DONE.toString());
        setDisplay(States.LOADING);

        ui.setColors(colors);
        ui.setBombs(bombs);
        ui.setPowerups(powerups);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                c.exit();
            }
        });
    }


    /**
     * Hook into the connection events.
     */
    private void hookEvents()
    {
        c.addConnectionStatusListener(this);
        c.addMessageListener(this);
        c.addGameStateListener(this);

        c.queueMessage(MetaMessage.connectSpectator());
    }


    /**
     * Switch the currently displayed card.
     *
     * @param state
     */
    public void setDisplay(States state)
    {
        layout.show(getContentPane(), state.toString());
    }


    @Override
    public void newMessage(IMessage m)
    {
        if(m instanceof MapMessage)
        {
            handleMap((MapMessage)m);
        }
        else if(m instanceof PosMessage)
        {
            handlePosition((PosMessage)m);
        }
        else if(m instanceof BombMessage)
        {
            handleBomb((BombMessage)m);
        }
        else if(m instanceof PowerupMessage)
        {
            handlePowerup((PowerupMessage)m);
        }
    }


    @Override
    public void statusChanged(State s)
    {
        // TODO Auto-generated method stub
    }


    /**
     * Handles a map change.
     *
     * @param m
     */
    private void handleMap(MapMessage m)
    {
        Board b = m.getBoard();
        ui.setMap(b);
        b.setPlayers(positions);
        ui.repaint();
    }


    /**
     * Handles a position message.
     *
     * @param m
     */
    private void handlePosition(PosMessage m)
    {
        Position pos = m.getPosition();
        PlayerTypes type = m.getType();
        int id = m.getPid();

        if(pos.getX() < 0 || pos.getY() < 0)
        {
            positions.remove(id);
            colors.remove(id);
        }
        else
        {
            positions.put(id, pos);
            Color c;

            switch(type)
            {
            case MONSTER:
                c = Color.CYAN;
                break;
            default:
                c = Color.BLUE;
            }

            colors.put(id, c);
        }

        // force ui update
        ui.repaint();
    }


    /**
     * Handles messages related to bombs.
     *
     * @param m
     */
    private void handleBomb(BombMessage m)
    {
        if(m.getSize() < 0)
        {
            bombs.remove(m.getPosition());
        }
        else
        {
            bombs.put(m.getPosition(), m.getSize());
        }

        ui.repaint();
    }


    /**
     * Handles powerups.
     *
     * @param m
     */
    private void handlePowerup(PowerupMessage m)
    {
        switch(m.getAction())
        {
        case ADD:
            powerups.add(m.getPosition());
            break;
        case REMOVE:
            powerups.remove(m.getPosition());
        }

        ui.repaint();
    }


    @Override
    public void stateChanged(StateMessage.State state)
    {
        switch(state)
        {
        case END:
            setDisplay(States.DONE);
            break;
        case STARTED:
            setDisplay(States.GAME);
            break;
        case NOTSTARTED:
            loading_label.setText("Connected. Game as not started yet.");
        }
    }
}
