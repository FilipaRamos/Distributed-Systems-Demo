package ClientServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	// saves the port
	public int srvc_port;
	// class constructor
	public Server(){}
	// database
	public ArrayList<String> database = new ArrayList<String>();
	// owner found
	public String owner_name;

	public static void main(String argv[]) throws Exception{
		if(argv.length != 1){
			System.out.println("Wrong number of arguments!");
		}
		
		// create a server
		Server server = new Server();
		
		// fill database
		server.database.add("25-AB-25 Manuel");
		server.database.add("75-MB-98 Marina");
		
		// get the port
		server.srvc_port = Integer.parseInt(argv[0]);
		
		String clientRequest;          
		String statusResponse = null;
		
		// start the server
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(server.srvc_port);  
		System.out.println("Server listening...");
		
		while(true){       
			// accept the socket
			Socket connectionSocket = serverSocket.accept();
			
			// get the client's request
			BufferedReader inFromClient = new BufferedReader
					(new InputStreamReader(connectionSocket.getInputStream()));  
			clientRequest = inFromClient.readLine();   
			System.out.println("Received: " + clientRequest); 
			
			// process the request
			String[] splitted;
			splitted = clientRequest.split(" ");
			
			if(splitted[0].equals("register")){
				if(server.register(splitted[1], splitted[2]) == 1){
					System.out.println("register succesful!");
					statusResponse = "registered" + "\n";
				}
				else{
					System.out.println("plate already on the database");
					statusResponse = "already registered" + "\n";
				}
			}
			else if(splitted[0].equals("lookup")){
				if(server.lookup(splitted[1]) == 1){
					System.out.println("lookup succesful!");
					statusResponse = "found the plate! It belongs to " + server.owner_name + "\n";
				}
				else{
					System.out.println("plate was not found!");
					statusResponse = "plate does not exist" + "\n";
				}
			}
			
			// send the response
			DataOutputStream outToClient = new DataOutputStream
					(connectionSocket.getOutputStream());                   
			outToClient.writeBytes(statusResponse);

			System.out.println("finalized");
		} 
	}
	
	// process register operations
	public int register(String plate_number, String owner_name){
		String newPlate = plate_number + " " + owner_name;
		if(database.contains(newPlate))
			return 2;
		else{
			database.add(newPlate);
			return 1;
		}
	}
	
	// process lookup operations
	public int lookup(String plate_number){
		for(int i = 0; i < database.size(); i++){
			String[] data = database.get(i).split(" ");
			if(data[0].equals(plate_number)){
				owner_name = data[1];
				return 1;
			}
		}
		return 2;
	}
}
