package carleton.sysc3303.server;

/**
 * Configuration for the server.
 */
public class Config
{
    public int
        defaultMoveTime, fastMoveTime, defaultBomb,
        defaultExplosionSize, bombTimer, explosionDuration;


    /**
     * Gets the default configuration.
     *
     * @return
     */
    public static Config defaultConfig()
    {
        Config c = new Config();

        c.defaultMoveTime = 300;
        c.fastMoveTime = 200;
        c.defaultBomb = 1;
        c.defaultExplosionSize = 2;
        c.bombTimer = 2000;
        c.explosionDuration = 1000;

        return c;
    }
    
    public void setMoveTime(int t)
    {
    	defaultMoveTime = t;
    	fastMoveTime = t - t/3;
    }
    
    public void setBomb(int b)
    {
    	defaultBomb = b;
    }
    
    public void setDefaultExplosionSize(int s)
    {
    	defaultExplosionSize = s;
    }
}
