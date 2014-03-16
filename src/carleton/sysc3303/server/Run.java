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
        IServer s = new UDPServer(9999, 50);
        ServerBoard b;

        if(args.length == 1)
        {
            b = ServerBoard.fromFile(new File(args[0]));
        }
        else
        {
            b = new ServerBoard(20);
        }

        new GameBoard(s, b);
        new Thread(s).start(); // background the server

        System.out.println("Started");
    }
}
