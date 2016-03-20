import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	
	public String input;
	public String operation;
	public String id;
	public String path;
	public int replicationDegree;
	
	public Multicast multicast = new Multicast("224.0.0.3", 8884, "224.0.0.26", 8885, "224.0.0.116", 8886);
	public ArrayList<Message> messages = new ArrayList<Message>();
	
	public Server(){}
	
	public static void main(String args[]){
		
		Server server = new Server();
		server.parseInput(server);
		
		if(server.operation.equals("PUTCHUNK")){
			
			
			
		}
		
		
	}
	
	public void parseInput(Server server){
		
		Scanner input = new Scanner(System.in);
		System.out.println("Operation to perform? ");
		server.input = input.nextLine();
		input.close();
		
		String[] inputSplitted;
		inputSplitted = server.input.split(" +");
		
		server.operation = inputSplitted[0];
		server.id = inputSplitted[1];
		server.path = inputSplitted[2];
		server.replicationDegree = Integer.parseInt(inputSplitted[3]);
		
	}
	
}
