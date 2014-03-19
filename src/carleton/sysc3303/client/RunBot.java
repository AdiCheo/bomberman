package carleton.sysc3303.client;

import java.io.File;
import java.io.IOException;
import java.net.*;
import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.PlayerTypes;

public class RunBot
{
    /**
     * Application entry point.
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException
    {
        IConnection c = new UDPConnection(InetAddress.getByName("localhost"), 9999);

        BotClient b = new BotClient(c, 300, PlayerTypes.PLAYER);
        b.setCommands(new File(args[0]));
        new Thread(c).start();

        b.waitForConnection();
        b.waitForCompletion();
        System.exit(0);
    }
}
