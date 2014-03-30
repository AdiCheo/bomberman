package carleton.sysc3303.client.gui;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

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
                                              GameStateListener,
                                              UserMessageListener
{
    public enum States { CONNECTING, CONFIGURE, LOADING, GAME, DONE };

    private static final long serialVersionUID = 7088369983891361413L;

    protected static Logger logger = Logger.getLogger("carleton.sysc3303.client.gui.Window");

    protected GameView ui;
    protected DisplayBoard board;
    protected CardLayout layout;
    protected StatusBar statusbar;
    protected ConnectPanel connectPanel;
    protected SetupPanel setupPanel;
    protected JPanel center;
    protected JPanel loadingPanel;
    protected JLabel loadingLabel;
    protected Set<Position> powerups;
    protected IConnection c;
    protected Set<Player> players;
    protected Map<Position, Integer> bombs;


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

        bombs = new HashMap<Position, Integer>();
        powerups = new HashSet<Position>();
        players = new HashSet<Player>();

        board = new DisplayBoard();
        ui = new GameView(board);

        init();
        hookEvents();
        connect();
    }


    /**
     * Initialize the GUI.
     */
    protected void init()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 420);
        setMinimumSize(getSize());
        setTitle("Bomberman");

        setLayout(new BorderLayout());

        center = new JPanel();
        add(center, BorderLayout.CENTER);

        statusbar = new StatusBar();
        add(statusbar, BorderLayout.SOUTH);

        layout = new CardLayout();
        center.setLayout(layout);

        //FIX player hitting start having to change windows and change back before moving
        setupPanel = new SetupPanel(c);

        loadingPanel = new JPanel();
        loadingLabel = new JLabel("Loading");
        loadingPanel.add(loadingLabel);

        JPanel done_panel = new JPanel();
        done_panel.add(new JLabel("Game over"));

        center.add(loadingPanel, States.LOADING.toString());
        center.add(setupPanel, States.CONFIGURE.toString());
        center.add(ui, States.GAME.toString());
        center.add(done_panel, States.DONE.toString());
        setDisplay(States.LOADING);

        board.setBombs(bombs);
        board.setPowerups(powerups);
        board.setPlayers(players);

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
    protected void hookEvents()
    {
        c.addConnectionStatusListener(this);
        c.addMessageListener(this);
        c.addGameStateListener(this);
        c.addUserMessageListener(this);
    }


    /**
     * Initializes connection to the server.
     */
    protected void connect()
    {
        c.queueMessage(MetaMessage.connectSpectator());
    }


    /**
     * Switch the currently displayed card.
     *
     * @param state
     */
    public void setDisplay(States state)
    {
        layout.show(center, state.toString());
    }


    @Override
    public synchronized void newMessage(IMessage m)
    {
        if(m instanceof MapMessage)
        {
            handleMap((MapMessage)m);
        }
        else if(m instanceof PlayerMessage)
        {
            handlePosition((PlayerMessage)m);
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
        board.setWalls(b);
        ui.repaint();
    }


    /**
     * Handles a position message.
     *
     * @param m
     */
    private void handlePosition(PlayerMessage m)
    {
        Player p = getPlayer(m);
        players.remove(p);

        if(m.getX() >= 0 && m.getY() >= 0)
        {
            players.add(p);
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
            powerups.clear();
            break;
        case STARTED:
            setDisplay(States.GAME);
            requestFocus();
            break;
        case NOTSTARTED:
            setDisplay(States.CONFIGURE);
        }
    }


    /**
     * Extracts a player object from a PlayerMessage.
     *
     * @param m
     * @return
     */
    private Player getPlayer(PlayerMessage m)
    {
        return new Player(m.getPid(), m.getX(), m.getY(), m.getType() == PlayerTypes.MONSTER, m.getName());
    }


    @Override
    public void newMessage(String s)
    {
        statusbar.setText(s);
    }


    @SuppressWarnings("serial")
    private class StatusBar extends JPanel
    {
        private JLabel text;

        public StatusBar()
        {
            super();

            text = new JLabel(" ");
            text.setHorizontalAlignment(SwingConstants.LEFT);
            Font f = new Font("Arial", 0, 12);
            text.setFont(f);

            setMinimumSize(new Dimension(100, 20));
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY));
            add(text);
        }


        public synchronized void setText(final String s)
        {
            text.setText(s);

            final Object that = this;

            new Thread() {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(5000);
                    }
                    catch (InterruptedException e)
                    {
                        return;
                    }

                    synchronized(that)
                    {
                        if(text.getText().equals(s))
                        {
                            text.setText(" ");
                        }
                    }
                }
            }.start();
        }
    }
}
