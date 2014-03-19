package carleton.sysc3303.server;

import java.util.Date;

import carleton.sysc3303.common.PlayerTypes;

/**
 * A class representing a player on the server.
 */
public class Player
{
    private static final int TIME_BETWEEN_MOVES = 250;

    private int id;
    private Date lastMoveTime;
    private int remainingBombs;
    private boolean dead;
    private String name;


    /**
     * Constructor.
     *
     * @param id
     */
    public Player(int id, String name)
    {
        this.id = id;
        this.lastMoveTime = new Date(0);
        this.remainingBombs = 1; // hardcoded limit
        this.dead = false;
        this.name = name;
    }


    /**
     * Gets the player's name.
     *
     * @return
     */
    public String getName()
    {
        return name;
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
     * Gets the time that the player last moved at.
     *
     * @return
     */
    public Date getLastMoveTime()
    {
        return lastMoveTime;
    }

    /**
     * Gets the current bomb count.
     *
     * @return
     */
    public int getBomb()
    {
        return remainingBombs;
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
        return !isDead() && (new Date().getTime() - lastMoveTime.getTime()) >= TIME_BETWEEN_MOVES;
    }


    /**
     * Decrements the number of bombs the player has at the moment.
     *
     * @return
     */
    public synchronized boolean decrementRemainingBombs()
    {
        if(remainingBombs > 0)
        {
            remainingBombs--;
            return true;
        }

        return false;
    }


    /**
     * Increments the number of available bombs.
     */
    public synchronized void incrementRemainingBombs()
    {
        remainingBombs++;
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
     * Gets the player type.
     *
     * @return
     */
    public PlayerTypes getType()
    {
        return PlayerTypes.PLAYER;
    }


    /**
     * Checks if player is dead or not.
     *
     * @return
     */
    public boolean isDead()
    {
        return dead;
    }


    /**
     * Sets the player's life/death status.
     *
     * @param dead
     */
    public void setDead(boolean dead)
    {
        this.dead = dead;
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
