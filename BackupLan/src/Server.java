import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {

	public String input;
	public String operation;
	public String id;
	public String path;
	public int replicationDegree;
	public int reclaimSpace;

	public CounterControl controlC;
	public CounterBackup backupC;
	public CounterRestore restoreC;

	public ProcessControl controlP;
	public ProcessBackup backupP;
	public ProcessRestore restoreP;

	public ServerManager serverManager;

	public Multicast multicast = new Multicast("224.0.0.3", 8884, "224.0.0.26", 8885, "224.0.0.116", 8886);

	// files that were asked to be stored by this server
	public ArrayList<FileEvent> files = new ArrayList<FileEvent>();
	// chunks that were stored by this server
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	// requests that the user has made
	public ArrayList<Message> requests = new ArrayList<Message>();
	// file to restore
	public FileEvent file = null;
	// chunks that are below the desired replication degree
	public ArrayList<Chunk> belowChunks = new ArrayList<Chunk>();

	public Scanner in = new Scanner(System.in);

	public Server() {
	}

	public static void main(String args[]) {

		Server server = new Server();
		server.startEngine();

		server.parseInput(server);

		try {
			Thread.sleep(400);
		} catch (Exception e) {
		}

		while (!server.input.equals("exit")) {

			if (server.operation.equals("PUTCHUNK")) {

				// create the specified file
				File file = new File(server.path);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				FileEvent fileEvent = new FileEvent(server.id, file.getName(), (int) file.length(),
						sdf.format(file.lastModified()), server.replicationDegree);

				server.files.add(fileEvent);

				fileEvent.splitFile(file, server);

				server.backupP = new ProcessBackup(server, server.serverManager);

			} else if (server.operation.equals("GETCHUNK")) {

				int index = server.findFile(server.path);
				server.file = server.files.get(index);
				server.files.get(index).chunks.clear();

				// add the messages for each chunk
				for (int i = 0; i < server.file.chunksNo; i++) {

					Message message = new Message("GETCHUNK", "1.0", server.id, server.file.identifier, i, 0, null);
					server.controlP.sendQueue.add(message);

				}

				try {
					Thread.sleep(5000);
				} catch (Exception e) {
				}

				server.file.mergeFile();

				System.out.println("Restore was successful!");

			} else if (server.operation.equals("DELETE")) {

				int deleteIndex = server.findFile(server.path);
				server.file = server.files.get(deleteIndex);

				Message deleteMessage = new Message("DELETE", "1.0", server.id, server.file.identifier, 0, 0, null);
				server.controlP.sendQueue.add(deleteMessage);

				try {
					Thread.sleep(2000);
				} catch (Exception e) {
				}

				server.file = null;
				server.files.remove(deleteIndex);

				System.out.println("Finished deleting file!");

			} else if (server.operation.equals("RECLAIM")) {

				System.out.println("Reclaiming space now...");
				ArrayList<Chunk> chunksToDelete = server.reclaimSpace();

				if (chunksToDelete != null) {

					for (int i = 0; i < chunksToDelete.size(); i++) {

						String newPath = System.getProperty("user.dir") + "\\" + server.chunks.get(i).identifier + "_"
								+ Integer.toString(server.chunks.get(i).index);

						Path path = Paths.get(newPath);

						try {
							Files.deleteIfExists(path);
							System.out.println("Deleted chunk " + server.chunks.get(i).identifier + "_"
									+ Integer.toString(server.chunks.get(i).index));
						} catch (IOException e) {
							e.printStackTrace();
						}

						Message chunkToDel = new Message("REMOVED", "1.0", server.id, chunksToDelete.get(i).identifier,
								chunksToDelete.get(i).index, 0, null);
						server.controlP.sendQueue.add(chunkToDel);

					}

					try {
						Thread.sleep(2000);
					} catch (Exception e) {
					}

					server.removeChunks(chunksToDelete);
				}

			}

			server.parseInput(server);

		}

		server.in.close();

	}

	public void parseInput(Server server) {

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

		} else if (inputSplitted[0].equals("RECLAIM")) {

			server.operation = inputSplitted[0];
			server.id = inputSplitted[1];
			server.reclaimSpace = Integer.parseInt(inputSplitted[2]);

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
		restoreP = new ProcessRestore(this);

	}

	public int findFile(String name) {

		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).name.equals(name))
				return i;
		}

		return -1;

	}

	public ArrayList<Chunk> reclaimSpace() {

		ArrayList<Chunk> delChunks = new ArrayList<Chunk>();
		int space = 0;

		for (int i = 0; i < chunks.size(); i++) {

			space += chunks.get(i).data.length;
			delChunks.add(chunks.get(i));

			if (space >= reclaimSpace) {
				System.out.println("Found the chunks to eliminate...");
				break;
			}

		}

		if (space < reclaimSpace) {

			System.out.println("Server doesn't have enough chunks to reclaim that much space... :(");
			return null;

		}

		return delChunks;

	}

	public void removeChunks(ArrayList<Chunk> toDel) {

		for (int i = 0; i < toDel.size(); i++) {

			for (int j = 0; j < chunks.size(); j++) {

				if (toDel.get(i) == chunks.get(j))
					chunks.remove(j);

			}

		}

	}

	public void processBelowChunks() {

		int dif = 0;
		
		for (int i = 0; i < belowChunks.size(); i++) {

			System.out.println("Reinitiating the backup protocol for a chunk below desired replication degree from server " + id);

			FileEvent file = new FileEvent(id, 1);
			file.chunks.add(belowChunks.get(i));
			
			dif = belowChunks.get(i).replicationDegree - belowChunks.get(i).actualRepDeg;

			Message message = new Message("PUTCHUNK", "1.0", id, belowChunks.get(i).identifier,
					belowChunks.get(i).index, dif, belowChunks.get(i).data);

			requests.add(message);

		}

	}

}
