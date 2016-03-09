import java.io.*;
import java.util.*;
import Interface.*;

public class Server implements Runnable {
	// id of the server
	public int id;
	// control address and port
	public String controlAddress;
	public int controlPort;
	// backup address and port
	public String backupAddress;
	public int backupPort;
	// restore address and port
	public String restoreAddress;
	public int restorePort;
	// disk
	public String disk;
	// the multicast object of the server
	public Multicast multicast;
	// the disk manager object of the server
	public ManageDisk manager;
	// operation to run
	public String operation;
	
	// server constructor
	public Server(int id, String controlAddress, int controlPort, 
			String backupAddress, int backupPort, 
			String restoreAddress, int restorePort,
			String disk, String request){
		
		this.id = id;
		this.controlAddress = controlAddress;
		this.controlPort = controlPort;
		this.backupAddress = backupAddress;
		this.backupPort = backupPort;
		this.restoreAddress = restoreAddress;
		this.restorePort = restorePort;
		this.disk = disk;
		this.operation = request;
		
	}
	
	// main
	public static void main(String[] args) throws UnsupportedEncodingException{
		if(args.length != 8){
			System.out.println("Wrong number of arguments!");
			System.exit(1);
		}

		System.out.println(" =================== ");
		
		System.out.println("Choose the operation: ");
		
		Scanner input = new Scanner(System.in);
		String option = input.nextLine();
	
		Server server = new Server(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]), 
				args[3], Integer.parseInt(args[4]), 
				args[5], Integer.parseInt(args[6]), args[7], option);
		
		//ServerFile file = new ServerFile(1, "ficheiro.pl", 128000, "Ana");
		
		new Thread(server).start();
		
		Browser browser = new Browser();
			
	}
	
	// the engine of the server which calls the needed procedures
	public int ServerEngine(){
		return 0;
	}

	@Override
	public void run() {
		multicast = new Multicast(this.controlAddress, this.controlPort, 
				this.backupAddress, this.backupPort,
				this.restoreAddress, this.restorePort);
		
		manager = new ManageDisk(this.disk);
		long space;
		
		while(true){
			
			manager.evaluateDisk();
			space = manager.freeSpace;
			
			String message = "Server " + id + " available size = " + String.valueOf(space) + " bytes";
			
			String response = multicast.ControlChannel(this.controlAddress, this.controlPort, message, operation);
			System.out.println(response);

			manager.evaluateDisk();
			space = manager.freeSpace;
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
}
