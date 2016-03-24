import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

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
	public Server(String id, String controlAddress, int controlPort,
			String backupAddress, int backupPort, String restoreAddress,
			int restorePort, String disk, String messageType,
			int replicationDegree) {

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
	public static void main(String[] args) throws NoSuchAlgorithmException,
			NumberFormatException, IOException {
		if (args.length != 10) {
			System.out.println("Wrong number of arguments!");
			System.exit(1);
		}

		Server server = new Server(args[0], args[1], Integer.parseInt(args[2]),
				args[3], Integer.parseInt(args[4]), args[5],
				Integer.parseInt(args[6]), args[7], args[8],
				Integer.parseInt(args[9]));

		Thread t = new Thread(){
			public void run(){
				try {
					server.userInterface(args[0]);
				} catch (NumberFormatException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
		
		server.multicast = new Multicast(server.controlAddress,
				server.controlPort, server.backupAddress, server.backupPort,
				server.restoreAddress, server.restorePort);

		if (args[8].equals("PUTCHUNK")) {
			server.serverFile.splitFile(server.file, server,
					server.replicationDegree);
			server.ServerEngine(server, "backup", server.serverFile,
					server.file);
		}

	}

	// the engine of the server which calls the needed procedures
	public void ServerEngine(Server server, String request, ServerFile file,
			File f) {

		ThreadEngine threadManager1 = new ThreadEngine(request, this, file, f);
		threadManager1.CreateThread(threadManager1);

		ThreadEngine listenThread = new ThreadEngine("listen backup", server,
				null, null);
		listenThread.CreateThread(listenThread);

	}

	// the user interface
	public void userInterface(String port) throws NumberFormatException,
			IOException {

		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));

		String clientRequest;
		String statusResponse = null;

		while (true) {
			Socket connectionSocket = serverSocket.accept();

			BufferedReader inFromClient = new BufferedReader(
					new InputStreamReader(connectionSocket.getInputStream()));
			clientRequest = inFromClient.readLine();
			System.out.println("Received: " + clientRequest);

			String[] splitted;
			splitted = clientRequest.split(" ");
						
			if (splitted[0].equals("BACKUP")) {
				this.replicationDegree = Integer.parseInt(splitted[2]);
				this.operation = "PUTCHUNK";
				statusResponse = "Backup " + splitted[1] + " Replication Degree: " + splitted[2]; 
			} else if (splitted[0].equals("RESTORE")) {
				this.operation = "GETCHUNK";
				statusResponse = "Restore " + splitted[1];
			} else if (splitted[0].equals("DELETE")) {
				this.operation = "DELETE";
				statusResponse = "Delete " + splitted[1];
			} else if (splitted[0].equals("RECLAIM")) {
				this.operation = "REMOVED";
				statusResponse = "Removed " + splitted[1];
			}
			
			DataOutputStream outToClient = new DataOutputStream
					(connectionSocket.getOutputStream());                   
			outToClient.writeBytes(statusResponse + "\n");
		}
	}
}
