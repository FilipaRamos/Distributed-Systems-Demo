import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	
	public static void main(String args[]) throws Exception {
		
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");

		System.out.println("Command :");

		Scanner input = new Scanner(System.in);
		String command = input.nextLine();
		System.out.println(command);
		String plate_number = input.nextLine();
		System.out.println(plate_number);

		if (command.equals("REGISTER")) {
			System.out.println("whattt???");
			String owner_name = input.nextLine();

			String register = "register" + " " + plate_number + " " + owner_name;

			sendData = register.getBytes();
			
			DatagramPacket registerAction = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			clientSocket.send(registerAction);
			
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			
			String dataReceived = new String(receivePacket.getData());
			System.out.println(dataReceived);

		}
		else if(command == "LOOKUP"){
			String lookup = "lookup" + " " + plate_number;
			
			sendData = lookup.getBytes();
			
			DatagramPacket lookupAction = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			clientSocket.send(lookupAction);
			
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			
			String found = new String(receivePacket.getData());
			System.out.println(found);
		}
		else{
			System.out.println("ERROR! Wrong command!");
		}
		
		clientSocket.close();
	}

}