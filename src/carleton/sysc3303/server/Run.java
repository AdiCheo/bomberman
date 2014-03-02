package carleton.sysc3303.server;

import carleton.sysc3303.server.connection.*;

public class Run
{
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        IServer s = new UDPServer(9999);
        GameBoard gb = new GameBoard(s, 9);

        new Thread(s).start(); // background the server

        System.out.println("Started");
    }
}
