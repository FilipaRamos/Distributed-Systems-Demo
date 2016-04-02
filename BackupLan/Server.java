import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Server {

	public String id = "1233";

	public CounterControl controlC;
	public CounterBackup backupC;
	public CounterRestore restoreC;

	public ProcessControl controlP;
	public ProcessBackup backupP;
	public ProcessRestore restoreP;

	public ServerManager serverManager;

	public Multicast multicast = new Multicast("236.0.0.1", 4445, "236.0.0.1", 4446, "236.0.0.1", 4447);

	// files that were asked to be stored by this server
	public ArrayList<FileEvent> files = new ArrayList<FileEvent>();
	// chunks that were stored by this server
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	// requests from the testApp
	public ArrayList<Request> requests = new ArrayList<Request>();
	// requests that the user has made
	public ArrayList<Message> putchunkRequests = new ArrayList<Message>();
	// file to restore
	public FileEvent restoreFile = null;
	// file to delete
	public FileEvent deleteFile = null;
	// chunks that cannot be stored
	public ArrayList<Chunk> forbiddenChunks = new ArrayList<Chunk>();

	public Server() {
	}

	public static void main(String args[]) {

		Server server = new Server();
		server.startEngine();

		Interface inter = new Interface(server);
		inter.getCommand();

		try {
			Thread.sleep(800);
		} catch (Exception e) {
		}

		while (true) {

			if (server.requests.size() != 0) {

				for (int i = 0; i < server.requests.size(); i++) {

					if (server.requests.get(i).type.equals("BACKUP")) {

						server.backupProtocol(i);
						server.requests.remove(i);

					} else if (server.requests.get(i).type.equals("RESTORE")) {

						server.restoreProtocol(i);
						server.requests.remove(i);

					} else if (server.requests.get(i).type.equals("DELETE")) {

						server.deleteProtocol(i);
						server.requests.remove(i);

					} else if (server.requests.get(i).type.equals("RECLAIM")) {

						server.reclaimProtocol(i);
						server.requests.remove(i);

					}

				}
			} else {}

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
		backupP = new ProcessBackup(this, serverManager);
		restoreP = new ProcessRestore(this);

	}

	public void backupProtocol(int i) {

		// create the specified file
		File file = new File(this.requests.get(i).path);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

		FileEvent fileEvent = new FileEvent(this.id, file.getName(), (int) file.length(),
				sdf.format(file.lastModified()), this.requests.get(i).replicationDegree);

		this.files.add(fileEvent);

		if (requests.get(i).enhanced)
			fileEvent.splitFile(file, this, true);
		else
			fileEvent.splitFile(file, this, false);

	}

	public void restoreProtocol(int i) {

		String version;
		int index = this.findFile(this.requests.get(i).path);

		this.restoreFile = this.files.get(index);
		this.files.get(index).chunks.clear();

		if (this.requests.get(i).enhanced) {

			version = "2.0";

		} else {

			version = "1.0";

		}

		// add the messages for each chunk
		for (int k = 0; k < this.restoreFile.chunksNo; k++) {

			Message message = new Message("GETCHUNK", version, this.id, this.restoreFile.identifier, k, 0, null);
			this.controlP.sendQueue.add(message);

		}

		while (this.restoreFile.chunks.size() != this.restoreFile.chunksNo) {
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}
		}

		this.restoreFile.mergeFile();

		System.out.println("Restore was successful!");

	}

	public void deleteProtocol(int i) {

		String version;
		int deleteIndex = this.findFile(this.requests.get(i).path);
		this.deleteFile = this.files.get(deleteIndex);

		if (this.requests.get(i).enhanced) {

			version = "2.0";

		} else {

			version = "1.0";

		}

		Message deleteMessage = new Message("DELETE", version, this.id, this.deleteFile.identifier, 0, 0, null);
		this.controlP.sendQueue.add(deleteMessage);

		try {
			Thread.sleep(2000);
		} catch (Exception e) {
		}

		this.deleteFile = null;
		this.files.remove(deleteIndex);

		System.out.println("Finished deleting file!");

	}

	public void reclaimProtocol(int i) {

		System.out.println("Reclaiming space now...");
		ArrayList<Chunk> chunksToDelete = this.reclaimSpace(i);
		String version;

		if (this.requests.get(i).enhanced) {

			version = "2.0";

		} else {

			version = "1.0";

		}

		if (chunksToDelete != null) {

			for (int k = 0; k < chunksToDelete.size(); k++) {

				String newPath = System.getProperty("user.dir") + "\\" + this.chunks.get(k).identifier + "_"
						+ Integer.toString(this.chunks.get(k).index);

				Path path = Paths.get(newPath);

				try {
					Files.deleteIfExists(path);
					System.out.println("Deleted chunk " + this.chunks.get(k).identifier + "_"
							+ Integer.toString(this.chunks.get(k).index));
				} catch (IOException e) {
					e.printStackTrace();
				}

				Message chunkToDel = new Message("REMOVED", version, this.id, chunksToDelete.get(k).identifier,
						chunksToDelete.get(k).index, 0, null);
				this.controlP.sendQueue.add(chunkToDel);

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}

				chunkToDel = new Message("REMOVED", version, this.id, chunksToDelete.get(k).identifier,
						chunksToDelete.get(k).index, 0, null);
				this.controlP.sendQueue.add(chunkToDel);

				// to keep track of the chunks that were deleted
				// by
				// this
				// peer and cannot be stored once again
				this.forbiddenChunks.add(chunksToDelete.get(k));

			}

			try {
				Thread.sleep(2000);
			} catch (Exception e) {
			}

			this.removeChunks(chunksToDelete);

		}

	}

	public int findFile(String name) {

		for (int i = 0; i < files.size(); i++) {
			if (files.get(i).name.equals(name))
				return i;
		}

		return -1;

	}

	public ArrayList<Chunk> reclaimSpace(int k) {

		ArrayList<Chunk> delChunks = new ArrayList<Chunk>();
		int space = 0;

		for (int i = 0; i < chunks.size(); i++) {

			space += chunks.get(i).data.length;
			delChunks.add(chunks.get(i));

			if (space >= this.requests.get(k).spaceToReclaim) {
				System.out.println("Found the chunks to eliminate...");
				break;
			}

		}

		if (space < this.requests.get(k).spaceToReclaim) {

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

}
