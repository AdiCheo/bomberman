package carleton.sysc3303.client;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.lang.reflect.Constructor;
import java.io.*;

import carleton.sysc3303.server.connection.*;
import carleton.sysc3303.common.connection.*;


public class BotClient implements Runnable
{
	private File commandList;
	private IClient c;
	private int buffer_size;
	private byte buffer[];
		
	public BotClient(File command, DatagramSocket server, int id, InetAddress address, int port, int buffer_size)
	{
		commandList = command;
		c = new UDPClient(server, id, address, port);
		this.buffer_size = buffer_size;
		this.buffer = new byte[buffer_size];
	}
	
	//Get method
	public IClient getClient()
	{
		return c;
	}
	
	public File getCommand()
	{
		return commandList;
	}
	
	//Run method
	public void run()
	{
		String line;
		BufferedReader reader;
		DatagramPacket send,receive;
		byte[] b;
		MoveMessage m;
		DatagramSocket socket;
		
		try
        {
            socket = new DatagramSocket();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            return;
        }
		
		//Initialize reader
		try
		{
			reader = new BufferedReader(new FileReader(commandList));
		}
			catch(FileNotFoundException e)
			{
            	e.printStackTrace();
            	socket.close();
            	return;
			}
		
		try
		{
			//Read lines
			while((line = reader.readLine()) != null)
			{
				m = new MoveMessage(line);//Create new message 
				b = m.serialize().getBytes();//Convert message into bytes		
				send = new DatagramPacket(b, b.length, c.getAddress(), c.getPort());
				receive =  new DatagramPacket(buffer, buffer_size);
				
				//Try sending message to server
		        try
		        {
		            socket.send(send);
		        }
		        	catch (IOException e)
			        {
			            e.printStackTrace();
			        }
		        
								
				/*
				 * Bot then waits for IMessage from server
				 */
		        
		        socket.receive(receive);	
		        parseMessage(buffer);
				
				//Wait half a second before processing next input
				try
				{
					Thread.sleep(500);
				}
					catch(InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}							
			}
		}
			catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
		
		//Close reader
		try
		{
			reader.close();
		}
			catch(IOException e)
			{
				e.printStackTrace();	
			}
		
		socket.close();
	}
	
	//Parse message from the server
	protected void parseMessage(byte[] data)
    {
        String[] msg = new String(data).split(":");
        @SuppressWarnings("rawtypes")
        Constructor c;
        IMessage m;
        int pid,x,y;//Used for position message

        try
        {
            c = Class.forName(msg[0]).getConstructor(new Class[]{String.class});
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            m = (IMessage)c.newInstance(msg[1]);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        if(m instanceof PosMessage)
        {
        	//Parse message
        	pid = ((PosMessage) m).getPid();
        	x = ((PosMessage) m).getX();
        	y = ((PosMessage) m).getY();
        	
        	//change GUI
        }
    }
}

