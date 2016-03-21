import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

	public String input;
	public String operation;
	public String id;
	public String path;
	public int replicationDegree;

	public CounterControl controlC;
	public CounterBackup backupC;

	public ProcessControl controlP;
	public ProcessBackup backupP;
	
	public ServerManager serverManager;

	public Multicast multicast = new Multicast("224.0.0.3", 8884, "224.0.0.26", 8885, "224.0.0.116", 8886);
	public FileEvent fileEvent;
	
	public ArrayList<Message> messages = new ArrayList<Message>();
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	public ArrayList<Message> requests = new ArrayList<Message>();

	public Server() {
	}

	public static void main(String args[]) {

		Server server = new Server();
		server.parseInput(server);

		server.startEngine();

		// create the specified file
		File file = new File(server.path);

		if (server.operation.equals("PUTCHUNK")) {

			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			server.fileEvent = new FileEvent(server.id, file.getName(), (int) file.length(),
					sdf.format(file.lastModified()));
			server.fileEvent.splitFile(file, server);
			
			server.backupP = new ProcessBackup(server);

		}

	}

	public void parseInput(Server server) {

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

	public void startEngine() {

		// create the counters
		controlC = new CounterControl(this);
		backupC = new CounterBackup(this);
		
		// create the processors
		controlP = new ProcessControl(this);
		
		// server manager
		serverManager = new ServerManager(this);

	}

}
