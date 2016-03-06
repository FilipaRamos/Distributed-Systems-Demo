package ServerClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
	// the multicastSocket to be used
	public DatagramSocket socket = null;

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

	// Create the DatagramSocket
	public void start() throws IOException {
		socket = new DatagramSocket(srvc_port);
		System.out.println("Launching Multicast Server Thread!");
		// set the socket timeout to 1 s so that it doesn't wait too long for a message to be received
		socket.setSoTimeout(1000);
	}

	@SuppressWarnings("null")
	public void serverEngine(Server server) throws IOException{

		while(true){
			
			// settle the IP_address
			InetAddress IP_address = InetAddress.getByName(mcast_addr);
			
			// store the messages
			byte[] bufferReceived = new byte[256];
			byte[] bufferSend = new byte[256];
			
			// message to advertise the IP address and port number
			String advertisement = "Server offers service in " + mcast_addr + srvc_port;
			
			// message to be sent
			String message = null;
			
			try{	
				// send the advertisement
				bufferSend = advertisement.getBytes();
				DatagramPacket toSend = new DatagramPacket(bufferSend, bufferSend.length, IP_address, srvc_port);
				socket.send(toSend);
				
				// get the client's request
				DatagramPacket receivedPacket = new DatagramPacket(bufferReceived, bufferReceived.length, IP_address, srvc_port);
				socket.receive(receivedPacket);
				String request = new String(receivedPacket.getData());
				System.out.println("Server received: " + request);
				
				// if a request was received
				if (request != null) {
					
					// perform the requested operation
					String[] splitted = request.split(" ");
					// register
					if (splitted[0].equals("register")) {
						System.out.println("Register operation requested. Plate number: " + splitted[1]
								+ ". Owner name: " + splitted[2]);
						server.register(splitted[1], splitted[2]);
						message = "register " + splitted[1] + " " + splitted[2];
					}
					// lookup
					if (splitted[0].equals("lookup")) {
						System.out.println("Lookup operation requested. Plate number: " + splitted[1]);
						String owner_name = server.lookup(splitted[1]);
						message = "lookup " + splitted[1] + " " + owner_name;
					} else { // error
						message = "error";
					}
				}
			}
			catch(IOException X){
				X.printStackTrace();
				break;
			}
			
		}
	}
	
	public void log(){ 
		String log = "multicast: " + 
				mcast_addr + mcast_port + 
				":" + srvc_port;
		System.out.print(log);
	}
	
	// add the plate to the database if it doesn't exist yet
	public void register(String plate_number, String owner_name){
		System.out.println("Adding register to the database...");

		String newPlate = plate_number + " " + owner_name;
		if(!database.contains(newPlate))
			database.add(newPlate);
		
		System.out.println("Plate added successfully!");
	}
	
	// find the plate and return the owner_name
	public String lookup(String plate_number){
		System.out.println("Looking for plate number...");
		
		String owner_name = null;
		
		for(int i = 0; i < database.size(); i++){
			String register = database.get(i);
			String[] splitted = register.split(" ");
			if(splitted[0].equals(plate_number)){
				owner_name = splitted[1];
			}
		}
		
		System.out.println("Plate found successfully!");
		return owner_name;
	}
}
