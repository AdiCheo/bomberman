package carleton.sysc3303.client.gui;

import javax.swing.*;
import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.Position;
import carleton.sysc3303.common.connection.MetaMessage;
import java.awt.*;

/**
 * The primary display window.
 *
 * @author Kirill Stepanov
 */
public class Window extends JFrame
{
    public enum States { LOADING, GAME };

    private static final long serialVersionUID = 7088369983891361413L;
    private GameView ui;
    private CardLayout layout;
    private JPanel loading_panel;
    private IConnection c;


    /**
     * Constructor
     *
     * @param ui
     */
    public Window(IConnection c)
    {
        this.c = c;
        this.ui = new GameView();
        init();
        hookEvents();
    }


    /**
     * Initialize the GUI.
     */
    private void init()
    {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setMinimumSize(getSize());
        setTitle("Bomberman");

        layout = new CardLayout();
        setLayout(layout);

        loading_panel = new JPanel();
        loading_panel.add(new JLabel("Loading"));

        add(loading_panel, States.LOADING.toString());
        add(ui, States.GAME.toString());
        setDisplay(States.LOADING);
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
            public void newMap(boolean[][] walls)
            {
                ui.setMap(walls);
                setDisplay(States.GAME);
            }
        });

        c.addPositionListener(new PositionListener() {
            @Override
            public void move(int object, Position pos)
            {
                // TODO Auto-generated method stub
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
