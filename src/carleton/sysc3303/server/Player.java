package carleton.sysc3303.server;

import java.util.Date;

/**
 * A class representing a player on the server.
 */
public class Player
{
    private static final int TIME_BETWEEN_MOVES = 250;

    private int id;
    private Date lastMoveTime;
    private boolean isMonster;


    /**
     * Constructor.
     *
     * @param id
     */
    public Player(int id, boolean b)
    {
        this.id = id;
        this.lastMoveTime = new Date(0);
        this.isMonster = b;
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

    public boolean getIsMonster()
    {
    	return isMonster;
    }

    /**
     * Gets the time that the player last moved at.
     *
     * @return
     */
    public Date getLastMoveTime()
    {
        return lastMoveTime;
    }


    /**
     * Sets the time the player last moved at.
     *
     * @param t
     */
    public void setLastMoveTime(Date t)
    {
        lastMoveTime = t;
    }


    /**
     * Checks if the player is allowed to move yet.
     *
     * @return
     */
    public boolean canMove()
    {
        return (new Date().getTime() - lastMoveTime.getTime()) >= TIME_BETWEEN_MOVES;
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
