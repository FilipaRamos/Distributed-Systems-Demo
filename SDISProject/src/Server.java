import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Server {
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
	// file chosen by the user
	public File file;
	// serverFile created
	public ServerFile serverFile;

	// server constructor
	public Server(int id, String controlAddress, int controlPort, String backupAddress, int backupPort,
			String restoreAddress, int restorePort, String disk) {

		this.id = id;
		this.controlAddress = controlAddress;
		this.controlPort = controlPort;
		this.backupAddress = backupAddress;
		this.backupPort = backupPort;
		this.restoreAddress = restoreAddress;
		this.restorePort = restorePort;
		this.disk = disk;

	}

	// main
	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, FileNotFoundException {
		if (args.length != 8) {
			System.out.println("Wrong number of arguments!");
			System.exit(1);
		}

		Server server = new Server(Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]), args[3],
				Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6]), args[7]);

		server.userInterface();

		server.multicast = new Multicast(server.controlAddress, server.controlPort, server.backupAddress,
				server.backupPort, server.restoreAddress, server.restorePort);
		
		// split the file in chunks
		server.serverFile.splitFile(server.file);
		server.ServerEngine(server, server.operation, server.serverFile, server.file);

	}

	// the engine of the server which calls the needed procedures
	public void ServerEngine(Server server, String request, ServerFile file, File f) {

		ThreadEngine threadManager1 = new ThreadEngine(request, this, file, f);
		threadManager1.CreateThread(threadManager1);

		// ThreadEngine threadManager2 = new ThreadEngine("request", this.multicast);
		// threadManager2.CreateThread(threadManager2);

	}
	
	// the user interface
	public void userInterface(){
		
		System.out.println(" =================== ");
		
		String curDir = System.getProperty("user.dir");
		System.out.println(curDir);

		System.out.println("What's the operation to perform?");

		Scanner input = new Scanner(System.in);
		
		operation = input.nextLine();
		
		System.out.println("What's the path of the file?");
		
		String path = input.nextLine();
		
		System.out.println("Who's the owner of the file?");
		
		String owner = input.nextLine();
		
		System.out.println(" =================== ");
		
		input.close();
		
		file = new File(path);
		boolean cenas = file.canRead();
		serverFile = new ServerFile(1, file.getName(), (int) file.length(), owner, Long.toString(file.lastModified()));
		System.out.println(file.getName() + " --> " + file.getPath() + " --> " + file.getTotalSpace() + " | " + cenas);
		
	}

}
