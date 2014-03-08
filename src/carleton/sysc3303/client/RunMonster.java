package carleton.sysc3303.client;

import java.io.File;
import java.net.*;
import carleton.sysc3303.client.connection.*;

public class RunMonster
{
    /**
     * Application entry point.
     *
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException
    {
    	IConnection c = new UDPConnection(InetAddress.getByName("localhost"), 9999);
               
        new BotClient(c, new File(args[0]), 333,Types.MONSTER);

        new Thread(c).start();
    }
}
