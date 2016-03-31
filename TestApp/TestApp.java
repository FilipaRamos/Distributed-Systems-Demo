import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestApp  {
	//IP address and Port number used by the initiator
	public String peer;
	
	//BACKUP / RESTORE / DELETE / RECLAIM
	public String protocol;
	
	//Path name or Amount of space to reclaim
	public String opnd_1;
	
	//Replication degree
	public int rep;
	
	public TestApp(){
	}
	
	public static void main (String args[]) throws NumberFormatException, UnknownHostException, IOException{
		if (args.length < 4 || args.length > 5) {
			System.out.println("Wrong number of arguments!");
		}
		
		TestApp app = new TestApp();
		
		Socket appSocket;

		app.protocol = args[1];
		app.opnd_1 = args[2];
		app.rep = Integer.parseInt(args[3]);
		
		if(args[0].indexOf(":") != -1){
		
			String[] splitted;
			splitted = (app.peer).split(":");
			
			appSocket = new Socket(splitted[0], Integer.parseInt(splitted[1]));
			app.peer = splitted[1];
			
		}else{
			
			appSocket = new Socket("localhost", Integer.parseInt(args[0]));
			app.peer = args[0];
			
		}
		
		System.out.println("Estabilished communication with initiator peer");
		
		String toSend = null;
		String response = null;
		
		if (app.protocol.equals("BACKUP")){
			toSend = "BACKUP " + args[0] + " " + args[2] + " " + args[3];
		}
		else if (app.protocol.equals("RESTORE")){
			toSend = "RESTORE " + args[0] + " " + args[2];
		}
		else if (app.protocol.equals("DELETE")){
			toSend = "DELETE " + args[0] + " " + args[2];
		}
		else if (app.protocol.equals("RECLAIM")){
			toSend = "RECLAIM " + args[0] + " " + args[2]; 
		}
		else {
			System.out.println("ERROR! Command not supported!");
			System.exit(0);
		}
		
		DataOutputStream outToServer = new DataOutputStream(appSocket.getOutputStream());
		outToServer.writeBytes(toSend + '\n');
		System.out.println("Sent command");
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(appSocket.getInputStream()));
		response = inFromServer.readLine();
		System.out.println("Response from server " + response);
		
		appSocket.close();
		
	}
}