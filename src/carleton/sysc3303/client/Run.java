package carleton.sysc3303.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import carleton.sysc3303.client.Window.States;
import carleton.sysc3303.client.connection.*;

public class Run
{
    private Window win;
    private GameView gv;
    private IConnection conn;


    /**
     * Application entry point.
     *
     * @param args
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        GameView gv = new GameView();
        Window w = new Window(gv);
        IConnection c = new DummyConnection(1000);

        new Run(w, gv, c);

        System.out.println("Started");
    }


    /**
     * Constructor.
     *
     * @param w
     * @param g
     * @param c
     */
    public Run(Window w, GameView g, IConnection c)
    {
        this.win = w;
        this.gv = g;
        this.conn = c;

        init();

        win.setVisible(true);
    }


    /**
     * Initializes the event listeners.
     */
    @SuppressWarnings("serial")
    private void init()
    {
        conn.addConnectedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println("connected");
                win.setDisplay(States.GAME);
            }
        });

        conn.addMapListener(new MapListener() {
            @Override
            public void newMap(List<Position> blocks)
            {
                gv.setMap(blocks);
                System.out.println("map load");
            }
        });
    }
}
