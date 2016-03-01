package ServerClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Server {
	// port number in which the server provides the service
	public static int srvc_port;
	// IP address of the multicast group used by the server
	public static String mcast_addr;
	// multicast group port number
	public static int mcast_port;
	// the datagramSocket to be used
	public DatagramSocket socket;
	// time interval between communications
	public int time_interval = 3000;

	// Class constructor
	public Server() {
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Wrong number of arguments!");
		}

		srvc_port = Integer.parseInt(args[0]);
		System.out.println(srvc_port);
		mcast_addr = args[1];
		System.out.println(mcast_addr);
		mcast_port = Integer.parseInt(args[2]);
		System.out.println(mcast_port);

		Server server = new Server();
		server.start();
		server.serverEngine(server);

	}

	@SuppressWarnings("resource")
	public void start() throws SocketException {
		socket = new DatagramSocket(srvc_port);
		System.out.println("Launching Multicast Server Thread!");
		System.out.println("Wainting for Client to connect...");
	}

	public void serverEngine(Server server) throws IOException{

		while(true){
			try{
				// store the message
				byte[] buffer = new byte[256];
				String message = "DONE";
				buffer = message.getBytes();
			
				// settle the IP_address
				InetAddress IP_address = InetAddress.getByName(mcast_addr);
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, IP_address, srvc_port);
			
				socket.send(packet);
				try // wait until it is time to send again
				{ Thread.sleep(time_interval);}
				catch (InterruptedException X) {X.printStackTrace();}
			}
			catch(IOException X){
				X.printStackTrace();
				break;
			}
		}
	}
}
