import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Server {
	// id of the server
	public String id;
	// type of the message
	public String messageType;
	// replication degree for the chunks
	public int replicationDegree;
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
	public Server(String id, String controlAddress, int controlPort, String backupAddress, int backupPort,
			String restoreAddress, int restorePort, String disk, String messageType, int replicationDegree) {

		this.id = id;
		this.controlAddress = controlAddress;
		this.controlPort = controlPort;
		this.backupAddress = backupAddress;
		this.backupPort = backupPort;
		this.restoreAddress = restoreAddress;
		this.restorePort = restorePort;
		this.disk = disk;
		this.messageType = messageType;
		this.replicationDegree = replicationDegree;

	}

	// main
	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException, FileNotFoundException {
		if (args.length != 10) {
			System.out.println("Wrong number of arguments!");
			System.exit(1);
		}

		Server server = new Server(args[0], args[1], Integer.parseInt(args[2]), args[3],
				Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6]), args[7], args[8], Integer.parseInt(args[9]));

		server.userInterface();

		server.multicast = new Multicast(server.controlAddress, server.controlPort, server.backupAddress,
				server.backupPort, server.restoreAddress, server.restorePort);
		
		if(args[8].equals("PUTCHUNK")){
			server.serverFile.splitFile(server.file, server, server.replicationDegree);
			server.ServerEngine(server, "backup", server.serverFile, server.file);
		}

	}

	// the engine of the server which calls the needed procedures
	public void ServerEngine(Server server, String request, ServerFile file, File f) {

		ThreadEngine threadManager1 = new ThreadEngine(request, this, file, f);
		threadManager1.CreateThread(threadManager1);

		ThreadEngine listenThread = new ThreadEngine("listen backup", server, null, null);
		listenThread.CreateThread(listenThread);

	}
	
	// the user interface
	public void userInterface(){
		
		System.out.println(" =================== ");
		
		String curDir = System.getProperty("user.dir");
		System.out.println(curDir);

		//System.out.println("What's the operation to perform?");

		Scanner input = new Scanner(System.in);
		
		//operation = input.nextLine();
		
		System.out.println("What's the path of the file?");
		
		String path = input.nextLine();
		
		System.out.println("Who's the owner of the file?");
		
		String owner = input.nextLine();
		
		System.out.println(" =================== ");
		
		input.close();
		
		file = new File(path);
		boolean cenas = file.canRead();
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		
		serverFile = new ServerFile(1, file.getName(), (int) file.length(), owner, sdf.format(file.lastModified()));
		System.out.println(file.getName() + " --> " + file.getPath() + " --> " + file.getTotalSpace() + " | " + cenas + " | " + serverFile.date);
		
	}

}
