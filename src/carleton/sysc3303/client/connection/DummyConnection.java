package carleton.sysc3303.client.connection;

import java.awt.event.ActionListener;
import java.util.*;

import carleton.sysc3303.client.GameView;
import carleton.sysc3303.client.Types;


/**
 * A fake connection that can be used for testing.
 *
 * @author Kirill Stepanov
 */
public class DummyConnection implements IConnection
{
    private List<MoveListener> moveListeners;
    private List<ObjectCreatedListener> objectCreatedListeners;
    private List<MapListener> mapListeners;
    private List<ActionListener> connectedListeners, disconnectedListeners;


    /**
     * Constructor.
     *
     * @param wait
     */
    public DummyConnection(final int wait)
    {
        moveListeners = new LinkedList<MoveListener>();
        objectCreatedListeners = new LinkedList<ObjectCreatedListener>();
        mapListeners = new LinkedList<MapListener>();
        connectedListeners = new LinkedList<ActionListener>();
        disconnectedListeners = new LinkedList<ActionListener>();

        // create a thread to generate events
        new Thread() {
            public void run()
            {
                try
                {
                    Thread.sleep(wait);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                invokeConnectedListeners();

                ArrayList<Position> tmp = new ArrayList<Position>();
                for(int i=1; i<GameView.BOARD_SIZE-1; i++)
                {
                    for(int j=1; j<GameView.BOARD_SIZE-1; j++)
                    {
                        tmp.add(new Position(i, j));
                    }
                }

                invokeMapListeners(tmp);
            }
        }.start();
    }


    @Override
    public void addMoveListener(MoveListener e)
    {
        moveListeners.add(e);
    }


    @Override
    public void addObjectCreatedListener(ObjectCreatedListener e)
    {
        objectCreatedListeners.add(e);
    }


    @Override
    public void addMapListener(MapListener e)
    {
        mapListeners.add(e);
    }


    @Override
    public void addConnectedListener(ActionListener e)
    {
        connectedListeners.add(e);
    }


    @Override
    public void addDisconnectedListener(ActionListener e)
    {
        disconnectedListeners.add(e);
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param obj
     * @param old
     * @param new_
     */
    private void invokeMoveListeners(int obj, Position old, Position new_)
    {
        for(MoveListener e: moveListeners)
        {
            e.move(obj, old, new_);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param type
     * @param pos
     */
    private void invokeObjectCreatedListeners(Types type, Position pos)
    {
        for(ObjectCreatedListener e: objectCreatedListeners)
        {
            e.objectCreated(type, pos);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     *
     * @param blocks
     */
    private void invokeMapListeners(List<Position> blocks)
    {
        for(MapListener e: mapListeners)
        {
            e.newMap(blocks);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     */
    private void invokeConnectedListeners()
    {
        for(ActionListener e: connectedListeners)
        {
            e.actionPerformed(null);
        }
    }


    /**
     * Invoke all listeners bound to this event.
     */
    private void invokeDisconnectedListeners()
    {
        for(ActionListener e: disconnectedListeners)
        {
            e.actionPerformed(null);
        }
    }
}
