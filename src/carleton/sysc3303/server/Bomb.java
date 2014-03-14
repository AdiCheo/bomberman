package carleton.sysc3303.server;

public class Bomb implements Runnable
{
    private static int counter = 0;
    private int id;
    private int owner;
    private int timeout;
    private BombExplodedListener listener;


    /**
     * Constructor.
     *
     * @param owner
     * @param timeout
     */
    public Bomb(int owner, int timeout)
    {
        this.id = counter++;
        this.owner = owner;
        this.timeout = timeout;
    }


    /**
     * Sets the listner that is called when the bomb explodes.
     *
     * @param e
     */
    public void setListener(BombExplodedListener e)
    {
        listener = e;
    }


    /**
     * Gets the bomb's id.
     *
     * @return
     */
    public int getId()
    {
        return id;
    }


    @Override
    public void run()
    {
        try
        {
            Thread.sleep(timeout);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            return;
        }

        listener.bombExploded(owner, id);
    }
}
