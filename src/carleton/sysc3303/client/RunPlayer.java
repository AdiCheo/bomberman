package carleton.sysc3303.client;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import carleton.sysc3303.client.connection.*;
import carleton.sysc3303.client.gui.ConnectPanel;
import carleton.sysc3303.client.gui.PlayerWindow;
import carleton.sysc3303.client.gui.Window;

public class RunPlayer
{
    /**
     * Application entry point.
     *
     * @param args
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException
    {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<IConnection> ic = executor.submit(new ConnectPanel());

        Window w = new PlayerWindow(ic.get());
        w.setVisible(true);
    }
}
