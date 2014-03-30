package carleton.sysc3303.common.connection;

public class ConfigureMessage implements IMessage
{
    private int numOfBombs,bombRange,delay,updateRate;


    /**
     * Constructor.
     *
     * @param b		number of bombs a player can use at once
     * @param r		range bombs have when they explode
     * @param x		delay between moves players can make
     * @param y		rate at which the server updates the game
     */
    public ConfigureMessage(int b, int r, int d, int u)
    {
        numOfBombs = b;
        bombRange = r;
        delay = d;
        updateRate = u;
    }
    
    /**
     * get numOfBombs
     * 
     * @return
     */
    public int getNumOfBombs()
    {
    	return numOfBombs;
    }
    
    /**
     * get bombRange
     * 
     * @return
     */
    public int getBombRange()
    {
    	return bombRange;
    }
    /**
     * Get delay
     * 
     * @return
     */
    public int getDelay()
    {
    	return delay;
    }
    
    /**
     * Get updateRate
     * 
     * @return
     */
    public int getUpdateRate()
    {
    	return updateRate;
    }


    /**
     * Unserializing constructor.
     *
     * @param data
     */
    public ConfigureMessage(String data)
    {
        String[] args = data.split(",");

        numOfBombs = Integer.parseInt(args[0]);
        bombRange = Integer.parseInt(args[1]);
        delay = Integer.parseInt(args[2]);
        updateRate = Integer.parseInt(args[3]);
    }


    @Override
    public String serialize()
    {
        return String.format("%d,%d,%d,%d", numOfBombs, bombRange, delay, updateRate);
    }
}
