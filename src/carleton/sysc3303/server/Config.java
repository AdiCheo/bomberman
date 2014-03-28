package carleton.sysc3303.server;

/**
 * Configuration for the server.
 */
public class Config
{
    public int
        defaultMoveTime, fastMoveTime,
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
        c.defaultMoveTime = 200;
        c.defaultExplosionSize = 2;
        c.bombTimer = 2000;
        c.explosionDuration = 1000;

        return c;
    }
}
