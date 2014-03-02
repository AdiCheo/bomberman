package carleton.sysc3303.server;


/**
 * A class representing a player on the server.
 */
public class Player
{
    private int id;
    private char display;


    /**
     * Constructor.
     *
     * @param id
     */
    public Player(int id, char display)
    {
        this.id = id;
        this.display = display;
    }


    /**
     * Gets the player's id.
     *
     * @return
     */
    public int getId()
    {
        return id;
    }


    /**
     * Gets the player's character representation.
     *
     * @return
     */
    public char getDisplay()
    {
        return display;
    }


    /**
     * Calculates the object's hash.
     *
     * @return
     */
    public int hashCode()
    {
        return new Integer(id).hashCode();
    }


    /**
     * Checks equivalence with another object.
     *
     * @return
     */
    public boolean equals(Object o)
    {
        if(o == null || !(o instanceof Player))
        {
            return false;
        }

        if(o == this)
        {
            return true;
        }

        return ((Player)o).id == this.id;
    }
}
