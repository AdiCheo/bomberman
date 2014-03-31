package carleton.sysc3303.server;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.*;

import carleton.sysc3303.client.SmartBot;
import carleton.sysc3303.client.connection.IConnection;
import carleton.sysc3303.client.connection.UDPConnection;
import carleton.sysc3303.server.connection.*;

public class Run
{
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        for(Handler h: Logger.getLogger("").getHandlers())
        {
            h.setLevel(Level.WARNING);
        }

        IServer s = new UDPServer(9999, 50);
        ServerBoard b;

        if(args.length >= 1)
        {
            b = ServerBoard.fromFile(new File(args[0]));
        }
        else
        {
            b = new ServerBoard(20);
        }

        GameBoard logic = new GameBoard(s, b, Config.defaultConfig());

        for(int i=1; i<args.length; i++)
        {
            logic.addBoard(ServerBoard.fromFile(new File(args[i])));
        }

        new Thread(s).start(); // background the server

        System.out.println("Started");

        InetAddress local = InetAddress.getByName("localhost");

        for(int i=0; i<5; i++)
        {
            IConnection c = new UDPConnection(local, 9999);
            new SmartBot(c, 1000);
            new Thread(c).start();
        }
    }
}
