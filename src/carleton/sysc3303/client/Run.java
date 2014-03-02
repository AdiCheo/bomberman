package carleton.sysc3303.client;

import java.net.*;
import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.common.connection.MetaMessage;
import carleton.sysc3303.common.connection.MetaMessage.Type;

public class Run
{
    /**
     * Application entry point.
     *
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException
    {
        GameView gv = new GameView();
        IConnection c = new TestUDPConnection(InetAddress.getByName("localhost"), 9999);

        new Thread(c).start();

        c.queueMessage(new MetaMessage(Type.CONNECT, "foo"));
    }
}
