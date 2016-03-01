package ServerClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Server {
	// database
	public ArrayList<String> database = new ArrayList<String>();
	// port number in which the server provides the service
	public static int srvc_port;
	// IP address of the multicast group used by the server
	public static String mcast_addr;
	// multicast group port number
	public static int mcast_port;
	// the datagramSocket to be used
	public DatagramSocket socket;
	// time interval between communications (1 s)
	public int time_interval = 1000;

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
	}

	public void serverEngine(Server server) throws IOException{

		while(true){
			try{
				// store the message
				byte[] bufferReceived = new byte[256];
			
				// settle the IP_address
				InetAddress IP_address = InetAddress.getByName(mcast_addr);
				DatagramPacket packetReceived = new DatagramPacket(bufferReceived, bufferReceived.length, IP_address, srvc_port);
			
				socket.receive(packetReceived);
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
	
	public void log(){ // FALTA O PRINT DO SVRC_ADDR
		String log = "multicast: " + 
				mcast_addr + mcast_port + 
				":" + srvc_port;
		System.out.print(log);
	}
	
	// add the plate to the database if it doesn't exist yet
	public void register(String plate_number, String owner_name){
		System.out.println("Adding register to the database...");

		String newPlate = plate_number + " " + owner_name;
		if(database.contains(newPlate))
			database.add(newPlate);
		
		System.out.println("Plate added successfully!");
		
	}
	
	// find the plate and return the owner_name
	public String lookup(String plate_number){
		
		String owner_name = null;
		
		for(int i = 0; i < database.size(); i++){
			String register = database.get(i);
			String[] splitted = register.split(" ");
			if(splitted[0].equals(plate_number)){
				owner_name = splitted[1];
			}
		}
		
		return owner_name;
		
	}
}
