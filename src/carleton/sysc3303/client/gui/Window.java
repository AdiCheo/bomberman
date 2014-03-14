package carleton.sysc3303.client.gui;

import javax.swing.*;
import java.awt.*;
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
public class Window extends JFrame
{
    public enum States { LOADING, GAME, DONE };

    private static final long serialVersionUID = 7088369983891361413L;
    private GameView ui;
    private CardLayout layout;
    private JPanel loading_panel;
    private IConnection c;
    private Map<Integer, Position> positions;
    private Map<Integer, Color> colors;
    private Map<Position, Integer> bombs;


    /**
     * Constructor
     *
     * @param ui
     */
    public Window(IConnection c)
    {
        this.c = c;
        this.ui = new GameView();
        positions = new ConcurrentHashMap<Integer, Position>();
        colors = new ConcurrentHashMap<Integer, Color>();
        bombs = new ConcurrentHashMap<Position, Integer>();

        init();
        hookEvents();
    }


    /**
     * Initialize the GUI.
     */
    private void init()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setMinimumSize(getSize());
        setTitle("Bomberman");

        layout = new CardLayout();
        setLayout(layout);

        loading_panel = new JPanel();
        loading_panel.add(new JLabel("Loading"));

        JPanel done_panel = new JPanel();
        done_panel.add(new JLabel("Game over"));

        add(loading_panel, States.LOADING.toString());
        add(ui, States.GAME.toString());
        add(done_panel, States.DONE.toString());
        setDisplay(States.LOADING);

        ui.setColors(colors);
        ui.setBombs(bombs);
    }


    /**
     * Hook into the connection events.
     */
    private void hookEvents()
    {
        c.addConnectionStatusListener(new ConnectionStatusListener() {
            @Override
            public void statusChanged(State s)
            {
                // TODO Auto-generated method stub
            }
        });

        c.addMapListener(new MapListener() {
            @Override
            public void newMap(Board b)
            {
                ui.setMap(b);
                b.setPlayers(positions);
                setDisplay(States.GAME);
                ui.repaint();
            }
        });

        c.addPositionListener(new PositionListener() {
            @Override
            public void move(int id, Position pos, PlayerTypes type)
            {
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
        });

        c.addGameStateListener(new GameStateListener() {
            @Override
            public void stateChanged(StateMessage.State state)
            {
                if(state == StateMessage.State.END)
                {
                    setDisplay(States.DONE);
                }
            }
        });


        c.addBombListener(new BombListener() {
            @Override
            public void bomb(Position pos, int size)
            {
                if(pos.getX() < 0 || pos.getY() < 0)
                {
                    bombs.remove(pos);
                }
                else
                {
                    bombs.put(pos, size);
                }

                ui.repaint();
            }
        });

        c.queueMessage(new MetaMessage(MetaMessage.Type.CONNECT, "0"));
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
}
