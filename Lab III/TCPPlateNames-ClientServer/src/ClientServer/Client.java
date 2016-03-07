package ClientServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	// received arguments
	public String host_name;
	public int port_number;
	public String operation;
	public ArrayList<String> operands;
	
	// constructor
	public Client(){}
	
	public static void main(String argv[]) throws Exception {
		if(argv.length < 4 || argv.length > 5){
			System.out.println("Wrong number of arguments!");
		}
		
		// create a client
		Client client = new Client();
		
		// save the given arguments
		client.host_name = argv[0];
		client.port_number = Integer.parseInt(argv[1]);
		client.operation = argv[2];
		
		// to save the request to be sent
		String toSend = null;
		// to save the response from the server
		String response = null;
		
		// register operation
		if(client.operation.equals("register")){
			// wrong operands number
			if(argv.length != 5){
				System.out.println("Wrong number of operands for operation register!");
			}
			
			// request to be sent
			toSend = "register " + argv[3] + " " + argv[4];
			
		}
		// lookup operation
		else if(client.operation.equals("lookup")){
			// wrong operands number
			if(argv.length != 4){
				System.out.println("Wrong number of operands for operation lookup!");
			}
			
			// request to be sent
			toSend = "lookup " + argv[3];
			
		}
		else{ // error situation
			System.out.println("ERROR! Command not supported!");
			System.exit(0);
		}
	
		Socket clientSocket = new Socket(client.host_name, client.port_number);
		
		// send request to server
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(toSend + '\n');
		System.out.println("Waiting for status report...");
		
		// get the server's response
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		response = inFromServer.readLine();
		System.out.println("Status from server: " + response);
		
		// close the socket
		clientSocket.close();
	}

}
