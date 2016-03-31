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
		
		app.peer = args[0];
		app.protocol = args[1];
		app.opnd_1 = args[2];
		app.rep = Integer.parseInt(args[3]);
		
		String[] splitted;
		splitted = (app.peer).split(":");
		
		String toSend = null;
		String response = null;
		
		if (app.protocol.equals("BACKUP")){
			toSend = "BACKUP " + args[2] + " " + args[3];
		}
		else if (app.protocol.equals("RESTORE")){
			toSend = "RESTORE " + args[2];
		}
		else if (app.protocol.equals("DELETE")){
			toSend = "DELETE " + args[2];
		}
		else if (app.protocol.equals("RECLAIM")){
			toSend = "RECLAIM " + args[2]; 
		}
		else {
			System.out.println("ERROR! Command not supported!");
			System.exit(0);
		}
		
		Socket appSocket = new Socket(splitted[0], Integer.parseInt(splitted[1]));
		
		DataOutputStream outToServer = new DataOutputStream(appSocket.getOutputStream());
		outToServer.writeBytes(toSend + '\n');
		System.out.println("Waiting for status report...");
		
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(appSocket.getInputStream()));
		response = inFromServer.readLine();
		System.out.println("Status from server: " + response);
		
		appSocket.close();
		
	}
}