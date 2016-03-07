package ServerClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class Server implements Runnable {
	// database
	public ArrayList<String> database = new ArrayList<String>();
	// port number in which the server provides the service
	public static int srvc_port;
	// IP address of the multicast group used by the server
	public static String mcast_addr;
	// multicast group port number
	public static int mcast_port;
	// the Socket to be used
	public static DatagramSocket socket = null;
	// the multicastSocket to be used
	public static MulticastSocket multicastSocket = null;
	//
	public static byte[] bufferMulticast = new byte[256];
	//
	public static InetAddress Multicast_IP_address;
	//
	public static InetAddress IP_address;
	//
	public static String advertisement;

	// Class constructor
	public Server() throws IOException {
		multicastSocket = new MulticastSocket(mcast_port);
		System.out.println("Launching Multicast Server Thread!");
	}

	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Wrong number of arguments!");
		}
		
		// store the messages
		byte[] bufferReceived = new byte[256];

		// settle the IP_address
		IP_address = InetAddress.getLocalHost();

		/*
		 * Multicast Thread
		 */

		srvc_port = Integer.parseInt(args[0]);
		mcast_addr = args[1];
		mcast_port = Integer.parseInt(args[2]);

		// settle the IP address
		Multicast_IP_address = InetAddress.getByName(mcast_addr);

		// message to advertise the IP address and port number
		String localhost = InetAddress.getLocalHost().getHostAddress();
		advertisement = srvc_port + " " + localhost;

		Server server = new Server();

		new Thread(server).start();

		server.serverEngine(server, bufferReceived);

	}

	public void serverEngine(Server server, byte[] bufferReceived) {

		while (true) {

			try {
				// message to be sent
				String status = null;
				String request = null;
				
				// initiate the socket
				socket = new DatagramSocket(srvc_port);

				// get the client's request
				DatagramPacket receivedPacket = new DatagramPacket(bufferReceived, bufferReceived.length);
				socket.receive(receivedPacket);
				request = new String(receivedPacket.getData());
				System.out.println("Server received the request: ");
				System.out.println(request);

				// perform the requested operation
				String[] splitted = request.split(" ");

				// register
				if (splitted[0].equals("register")) {
					System.out.println("Register operation requested. Plate number: " + splitted[1] + ". Owner name: "
							+ splitted[2]);
					server.register(splitted[1], splitted[2]);
					status = "register " + splitted[1] + " " + splitted[2] + "\n";
				}
				// lookup
				else if (splitted[0].equals("lookup")) {
					System.out.println("Lookup operation requested. Plate number: " + splitted[1]);
					String owner_name = server.lookup(splitted[1]);
					status = "lookup " + splitted[1] + " " + owner_name + "\n";
				} else { // error
					status = "error" + "\n";
				}
				
				// send the status
				System.out.println(receivedPacket.getAddress());
				System.out.println(srvc_port);
				DatagramPacket sendPacket = new DatagramPacket(status.getBytes(), status.getBytes().length, receivedPacket.getAddress(), srvc_port);
				socket.send(sendPacket);
				System.out.println("Server finalized!");
				
				socket.close();

			} catch (IOException X) {
				X.printStackTrace();
				break;
			}
		}
	}

	// print the log
	public void log() {
		String log = "multicast: " + mcast_addr + " " + mcast_port + " : " + srvc_port;
		System.out.print(log);
	}

	// add the plate to the database if it doesn't exist yet
	public void register(String plate_number, String owner_name) {
		System.out.println("Adding register to the database...");

		String newPlate = plate_number + " " + owner_name;
		if (!database.contains(newPlate))
			database.add(newPlate);

		System.out.println("Plate added successfully!");
	}

	// find the plate and return the owner_name
	public String lookup(String plate_number) {
		System.out.println("Looking for plate number...");

		String owner_name = null;

		for (int i = 0; i < database.size(); i++) {
			String register = database.get(i);
			String[] splitted = register.split(" ");
			if (splitted[0].equals(plate_number)) {
				owner_name = splitted[1];
			}
		}

		System.out.println("Plate found successfully!");
		return owner_name;
	}

	@Override
	public void run() {
		try {
			// set time to live
			multicastSocket.setTimeToLive(1);
			while (true) {
				// send the advertisement
				bufferMulticast = advertisement.getBytes();
				System.out.println(advertisement);

				DatagramPacket toSend = new DatagramPacket(bufferMulticast, bufferMulticast.length,
						Multicast_IP_address, mcast_port);
				multicastSocket.send(toSend);

				System.out.println("sent multicast");
				Thread.sleep(1000);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
