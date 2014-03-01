package carleton.sysc3303.client;

import javax.swing.*;
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
    private Component ui;
    private CardLayout layout;
    private JPanel loading_panel;


    /**
     * Constructor
     *
     * @param ui
     */
    public Window(Component ui)
    {
        this.ui = ui;
        init();
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
     * Switch the currently displayed card.
     *
     * @param state
     */
    public void setDisplay(States state)
    {
        layout.show(getContentPane(), state.toString());
    }
}
