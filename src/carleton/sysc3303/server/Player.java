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


    /**
     * Constructor.
     *
     * @param id
     */
    public Player(int id)
    {
        this.id = id;
        this.lastMoveTime = new Date(0);
        this.remainingBombs = 1; // hardcoded limit
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
        return (new Date().getTime() - lastMoveTime.getTime()) >= TIME_BETWEEN_MOVES;
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
            
            if(remainingBombs == 0)
            {
            	BombFactory bf = new BombFactory(3000,this);
            	new Thread(bf).start();
            }
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

class BombFactory implements Runnable
{
	int delay;
	Player p;
	
	public BombFactory(int d, Player p)
	{
		delay = d;
		this.p = p;
	}
	
	public void run()
	{
		try 
		{
			Thread.sleep(delay);
		}catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		
		p.incrementRemainingBombs();
	}
}
