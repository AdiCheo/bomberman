package carleton.sysc3303.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

import carleton.sysc3303.client.Window.States;
import carleton.sysc3303.client.connection.*;

public class Run
{
	private Window win;
	private GameView gv;
	private IConnection conn;


	/**
	 * Application entry point.
	 *
	 * @param args
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		GameView gv = new GameView();
		Window w = new Window(gv);
		IConnection c = new DummyConnection(1000);

		new Run(w, gv, c);

		System.out.println("Started");

		// testing attempt to connect
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		DatagramSocket clientSocket;
		InetAddress IPAddress;
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		try {
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName("localhost");

			//        	while (true){
			String sentence;

			sentence = new String ("new");

			//	  	      if (sentence.equalsIgnoreCase("q"))
			//			  {
			//	  	    	  clientSocket.close();
			//	  	    	  break;
			//	  	      }

			sendData = sentence.getBytes();
			sentence = ""; // reset sentence variable

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String modifiedSentence = new String(receivePacket.getData());
			System.out.println("FROM SERVER:" + modifiedSentence);
			//	        }

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	/**
	 * Constructor.
	 *
	 * @param w
	 * @param g
	 * @param c
	 */
	public Run(Window w, GameView g, IConnection c)
	{
		this.win = w;
		this.gv = g;
		this.conn = c;

		init();

		win.setVisible(true);
	}


	/**
	 * Initializes the event listeners.
	 */
	@SuppressWarnings("serial")
	private void init()
	{
		conn.addConnectedListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("connected");
				win.setDisplay(States.GAME);
			}
		});

		conn.addMapListener(new MapListener() {
			@Override
			public void newMap(List<Position> blocks)
			{
				gv.setMap(blocks);
				System.out.println("map load");
			}
		});
	}
}
