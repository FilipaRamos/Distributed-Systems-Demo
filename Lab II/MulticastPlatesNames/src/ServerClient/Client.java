package ServerClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {
	
	// multicast data read from the input
	public int mcast_port;
	public String mcast_addr;
	public String operator;
	
	// socket data gotten from the multicast communication
	String srvc_addr;
	int srvc_port;

	// constructor
	public Client() {}

	public static void main(String[] args) {
		if (args.length < 4 || args.length > 5) {
			System.out.println("Wrong number of arguments!");
		}

		// create a client
		Client client = new Client();

		// save the arguments
		client.mcast_addr = args[0];
		client.mcast_port = Integer.parseInt(args[1]);
		client.operator = args[2];

		// create the multicast socket
		MulticastSocket multicastSocket;

		try {
			///////////////////////////////////////////////////////////////////
			/////////////////////////// MULTICAST /////////////////////////////
			
			// settle the multicast IP address
			InetAddress Multicast_IP_address = InetAddress.getByName(client.mcast_addr);
			
			// join the group
			multicastSocket = new MulticastSocket(client.mcast_port);
			multicastSocket.joinGroup(Multicast_IP_address);
			System.out.println("Joined Multicast group!");

			// to save the received message over multicast
			byte buf[] = new byte[256];
			DatagramPacket advertisement = new DatagramPacket(buf, buf.length, Multicast_IP_address, client.mcast_port);
			System.out.println("Received message");
			multicastSocket.receive(advertisement);
			System.out.println("Received multicast message");
			
			///////////////////////////////////////////////////////////////////

			// split the string
			String data = new String(advertisement.getData()).substring(0, advertisement.getLength());
			String[] adv_split = data.split(" ");
			
			// get the port and the address of the socket
			client.srvc_port = Integer.parseInt(adv_split[0]);
			client.srvc_addr = adv_split[1];
			
			System.out.println(client.srvc_port);
			System.out.println(client.srvc_addr);
			
			multicastSocket.leaveGroup(Multicast_IP_address);

			// print the log
			client.log();

			// create the normal socket
			DatagramSocket socket = new DatagramSocket();
			InetAddress IP_address = InetAddress.getByName(client.srvc_addr);

			// store the request
			String toSend = null;
			byte[] sendRequest = new byte[256];
			
			// store the response
			String toReceive = null;
			byte[] response = new byte[256];

			// process request
			if (args[2].equals("register")) {
				if (args.length != 5) {
					System.out.println("Wrong number of arguments for command register.");
					System.exit(0);
				}
				toSend = "register " + args[3] + " " + args[4] + "\n";
			} else if (args[2].equals("lookup")) {
				if (args.length != 4) {
					System.out.println("Wrong number of arguments for command register.");
					System.exit(0);
				}
				toSend = "lookup " + args[3] + "\n";
			}

			// send request to server through the socket
			sendRequest = toSend.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendRequest, sendRequest.length, IP_address,
					client.srvc_port);
			socket.send(sendPacket);
			System.out.println("Awaiting status from server...");

			// receive the status from the server
			DatagramPacket receivePacket = new DatagramPacket(response, response.length);

			System.out.println("received |||||||||");
			socket.receive(receivePacket);
			System.out.println("received");
			// received information
			toReceive = new String(receivePacket.getData());
			System.out.println("Status from server:");
			
			// close the socket
			socket.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// print the log
	public void log() {
		String log = "multicast: " + mcast_addr + " " + mcast_port + " : " + srvc_addr + " " + srvc_port;
		System.out.println(log);
	}
}
