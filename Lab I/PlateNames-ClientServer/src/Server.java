import java.net.*;
import java.util.ArrayList;

public class Server {
	public ArrayList<String> data = new ArrayList<String>();

	// constructor
	public Server(int pn) {
	}

	// register - returns number of plates on the database
	public int register(Server s, String plate_number, String owner_name) {
		String register = plate_number + " " + owner_name;

		if (!data.contains(register)) {
			data.add(register);
			return data.size();
		}

		System.out.println("ERROR! Plate number already on the database!");
		return -1;
	}

	// lookup
	public String lookup(String plate_number) {
		String[] found;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) == plate_number) {
				found = data.get(i).split(" ");
				return found[2];
			}
		}
		return "NOT_FOUND";
	}

	public static void main(String args[]) throws Exception {

		DatagramSocket serverSocket = new DatagramSocket(9876);
		Server s = new Server(9876);
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		while (true) {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			serverSocket.receive(receivePacket);
			
			String request = new String(receivePacket.getData());
			
			System.out.println("RECEIVED: " + request);
			
			InetAddress IPAddress = receivePacket.getAddress();
			
			int port = receivePacket.getPort();
			
			String[] split;
			
			split = request.split(" ");
			
			if(split[1] == "register"){
				int send = s.register(s, split[2], split[3]);
				
				if(send != -1){
					StringBuilder copy = new StringBuilder();
					copy.append("");
					copy.append(send);
					String dataSend = copy.toString();
					sendData = dataSend.getBytes();
				}
				else{
					sendData = "ERROR".getBytes();
				}
				
				DatagramPacket registerPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(registerPacket);
			}
			else if(split[1] == "lookup"){
				String owner = s.lookup(split[2]);
			
				sendData = owner.getBytes();
				
				DatagramPacket lookupPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(lookupPacket);
			}
			else{
				sendData = "ERROR".getBytes();
				
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			}	
		}
	}
}
