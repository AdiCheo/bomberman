package carleton.sysc3303.client;

import java.io.IOException;
import java.net.*;
import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.client.gui.Window;

public class RunSpectator
{
    /**
     * Application entry point.
     *
     * @param args
     *
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
        IConnection c = new UDPConnection(InetAddress.getByName("localhost"), 9999);
        Window w = new Window(c);

        new Thread(c).start();

        w.setVisible(true);
    }
}
