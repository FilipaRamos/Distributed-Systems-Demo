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
	public CounterRestore restoreC;

	public ProcessControl controlP;
	public ProcessBackup backupP;
	public ProcessRestore restoreP;

	public ServerManager serverManager;

	public Multicast multicast = new Multicast("224.0.0.3", 8884, "224.0.0.26",
			8885, "224.0.0.116", 8886);

	// files that were asked to be stored by this server
	public ArrayList<FileEvent> files = new ArrayList<FileEvent>();
	// chunks that were stored by this server
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	// requests that the user has made
	public ArrayList<Message> requests = new ArrayList<Message>();
	// file to restore
	public FileEvent file = null;

	public Scanner in = new Scanner(System.in);;

	public Server() {
	}

	public static void main(String args[]) {

		Server server = new Server();
		server.startEngine();

		server.parseInput(server);

		server.restoreP = new ProcessRestore(server, server.serverManager);
		
		System.out.println(server.operation);

		while (!server.input.equals("exit")) {

			if (server.operation.equals("PUTCHUNK")) {

				// create the specified file
				File file = new File(server.path);
				SimpleDateFormat sdf = new SimpleDateFormat(
						"MM/dd/yyyy HH:mm:ss");
				FileEvent fileEvent = new FileEvent(server.id, file.getName(),
						(int) file.length(), sdf.format(file.lastModified()),
						server.replicationDegree);

				server.files.add(fileEvent);

				fileEvent.splitFile(file, server);

				server.backupP = new ProcessBackup(server,
						server.serverManager, fileEvent);

			} else if (server.operation.equals("GETCHUNK")) {

				int index = server.findFile(server.path);
				server.file = server.files.get(index);
				server.files.get(index).chunks.clear();

				// add the messages for each chunk
				for (int i = 0; i < server.file.chunksNo; i++) {

					Message message = new Message("GETCHUNK", "1.0", server.id,
							server.file.identifier, i, 0, null);
					server.controlP.sendQueue.add(message);

				}

				server.file.mergeFile();

				System.out.println("Restore was successful!");

			} else if (server.operation.equals("DELETE")) {

				int index = server.findFile(server.path);
				System.out.println(index);
				server.file = server.files.get(index);
				server.files.get(index).chunks.clear();

				for (int i = 0; i < server.file.chunksNo; i++) {
					Message message = new Message("DELETE", "1.0", server.id,
							server.file.identifier, i, 0, null);
					server.controlP.sendQueue.add(message);
				}
			}

			server.parseInput(server);

		}

		server.in.close();

	}

	public void parseInput(Server server) {

		System.out.println("Operation to perform? ");
		server.input = in.nextLine();

		String[] inputSplitted;
		inputSplitted = server.input.split(" +");

		if (inputSplitted[0].equals("PUTCHUNK")) {

			server.operation = inputSplitted[0];
			server.id = inputSplitted[1];
			server.path = inputSplitted[2];
			server.replicationDegree = Integer.parseInt(inputSplitted[3]);

		} else if (inputSplitted[0].equals("GETCHUNK")) {

			server.operation = inputSplitted[0];
			server.id = inputSplitted[1];
			server.path = inputSplitted[2];

		} else if (inputSplitted[0].equals("DELETE")) {
			
			server.operation = inputSplitted[0];
			server.id = inputSplitted[1];
			server.path = inputSplitted[2];
		}

	}

	public void startEngine() {

		// server manager
		serverManager = new ServerManager(this);

		// create the counters
		controlC = new CounterControl(this, serverManager);
		backupC = new CounterBackup(this, serverManager);
		restoreC = new CounterRestore(this, serverManager);

		// create the processors
		controlP = new ProcessControl(this);

	}

	public int findFile(String name) {

		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).name.equals(name))
				return i;
		}

		return -1;

	}

}
