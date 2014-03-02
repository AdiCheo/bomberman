package carleton.sysc3303.server;
import java.io.File;
import java.io.IOException;

import carleton.sysc3303.server.connection.*;

public class Run
{
    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        IServer s = new UDPServer(9999);
        GameBoard gb;

        if(args.length == 1)
        {
            gb = new GameBoard(s, new File(args[0]));
        }
        else
        {
            gb = new GameBoard(s, 9);
        }

        new Thread(s).start(); // background the server

        System.out.println("Started");
    }
}
