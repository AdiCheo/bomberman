package carleton.sysc3303.client;

import java.io.File;
import java.io.IOException;
import java.net.*;
import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.PlayerTypes;

public class RunMonster
{
    /**
     * Application entry point.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        IConnection c = new UDPConnection(InetAddress.getByName("localhost"), 9999);

        new BotClient(c, 333, PlayerTypes.MONSTER).setCommands(new File(args[0]));
        new Thread(c).start();
    }
}
