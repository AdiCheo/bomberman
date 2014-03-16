package carleton.sysc3303.testing;

import static org.junit.Assert.*;

import java.io.File;
import java.net.InetAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import carleton.sysc3303.client.BotClient;
import carleton.sysc3303.client.connection.IConnection;
import carleton.sysc3303.client.connection.UDPConnection;
import carleton.sysc3303.common.PlayerTypes;
import carleton.sysc3303.server.GameBoard;
import carleton.sysc3303.server.ServerBoard;
import carleton.sysc3303.server.connection.IServer;
import carleton.sysc3303.server.connection.UDPServer;

public class PlayersTouch {

    @Before
    public void setUp() throws Exception
    {
        //Set up server
        IServer s = new UDPServer(9999, 50);
        ServerBoard b;

        b = new ServerBoard(20);

        new GameBoard(s, b);
        new Thread(s).start(); // background the server

        System.out.println("Started");

        //Place bot
        IConnection c = new UDPConnection(InetAddress.getByName("localhost"), 9999);

        new BotClient(c, 300, PlayerTypes.PLAYER).setCommands(new File("bot.txt"));
        new Thread(c).start();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test() {
        //wait for collision...

        //when collision happens
            //check if collision happened as expected
    }

}
