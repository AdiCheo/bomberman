import java.io.*;
import java.net.*;

class UDPClient {
   public static void main(String args[]) throws Exception {
      BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
      
      DatagramSocket clientSocket = new DatagramSocket();
      
      InetAddress IPAddress = InetAddress.getByName("localhost");
      
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      while (true){
	      String sentence = inFromUser.readLine();
	      
	      if (sentence == "q\n"){
	    	  clientSocket.close();
	    	  break;
	      }
	      
	      sendData = sentence.getBytes();
	      sentence = ""; // reset sentence variable
	      
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9999);
	      clientSocket.send(sendPacket);
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      clientSocket.receive(receivePacket);
	      String modifiedSentence = new String(receivePacket.getData());
	      System.out.println("FROM SERVER:" + modifiedSentence);
      }
   }
}
