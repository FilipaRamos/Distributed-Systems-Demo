import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class ServerManager implements Runnable {

	public Server server;
	public ArrayList<Message> messages;

	public int randomDelay;

	public ServerManager(Server server) {
		messages = new ArrayList<Message>();
		this.server = server;
		startServerManager();

	}

	public void startServerManager() {

		System.out.println("Server manager kicking off!");
		new Thread(this).start();

	}

	@Override
	public void run() {

		while (true) {

			for (int i = 0; i < messages.size(); i++) {

				// found a putchunk message so the chunk needs to be created and
				// stored
				if (messages.get(i).type.equals("PUTCHUNK")) {

					if (!messages.get(i).senderId.equals(server.id)) {

<<<<<<< HEAD
						System.out.println("Found a PUTCHUNK request! Storing chunk now...");
						System.out.println(messages.get(i).fileId + " " + messages.get(i).chunkNr);
=======
						System.out
								.println("Found a PUTCHUNK request! Storing chunk now...");
						System.out.println(messages.get(i).fileId + " "
								+ messages.get(i).chunkNr);
>>>>>>> master
						managePutchunk(i);
						messages.remove(i);

					}
				} else if (messages.get(i).type.equals("GETCHUNK")) {

					if (!messages.get(i).senderId.equals(server.id)) {

<<<<<<< HEAD
						try {
							Thread.sleep(800);
						} catch (Exception e) {
							e.printStackTrace();
						}

						System.out.println("Found a GETCHUNK request! Checking whether the chunk exists or not...");
=======
						System.out
								.println("Found a GETCHUNK request! Checking whether the chunk exists or not...");
>>>>>>> master
						processResponses(i);
						messages.remove(i);

					}
<<<<<<< HEAD

				} else if (messages.get(i).type.equals("DELETE")) {

					if (!messages.get(i).senderId.equals(server.id)) {

						try {
							Thread.sleep(800);
						} catch (Exception e) {
							e.printStackTrace();
						}

						System.out.println("Found a DELETE request ! Processing request...");
						if (server.chunks.size() != 0) {
							manageDelete(i);
							System.out.println("Deleted all chunks that belong to the file");
						} else {
							System.out.println("Server has no chunks");
						}

						messages.remove(i);

=======
				} else if (messages.get(i).type.equals("DELETE")) {
					if (!messages.get(i).senderId.equals(server.id)) {
						System.out
								.println("Found a DELETE request ! Deleting file and chunks...");
						if (messages.get(i).fileId
								.equals(server.chunks.get(i).identifier)) {
							server.chunks.remove(server.chunks.get(i));
							Path path = Paths.get(server.path);
							try {
								Files.deleteIfExists(path);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
>>>>>>> master
					}
				}
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void managePutchunk(int i) {

<<<<<<< HEAD
		Chunk chunk = new Chunk(messages.get(i).fileId, messages.get(i).chunkNr, messages.get(i).data,
=======
		Chunk chunk = new Chunk(messages.get(i).fileId,
				messages.get(i).chunkNr, messages.get(i).data,
>>>>>>> master
				messages.get(i).replicationDegree, 1);
		server.chunks.add(chunk);
		chunk.writeChunk();

<<<<<<< HEAD
		Message message = new Message("STORED", messages.get(i).version, server.id, messages.get(i).fileId,
				messages.get(i).chunkNr, 1, null);
=======
		Message message = new Message("STORED", messages.get(i).version,
				server.id, messages.get(i).fileId, messages.get(i).chunkNr, 1,
				null);
>>>>>>> master

		server.controlP.sendQueue.add(message);

	}

	public void manageGetchunk(int index) {

		for (int i = 0; i < server.chunks.size(); i++) {

<<<<<<< HEAD
			if (messages.get(index).fileId.equals(server.chunks.get(i).identifier)) {

				if (messages.get(index).chunkNr == server.chunks.get(i).index) {

					Message message = new Message("CHUNK", messages.get(index).version, server.id,
							messages.get(index).fileId, messages.get(index).chunkNr,
							messages.get(index).replicationDegree, server.chunks.get(i).data);
=======
			if (messages.get(index).fileId
					.equals(server.chunks.get(i).identifier)) {

				if (messages.get(index).chunkNr == server.chunks.get(i).index) {

					Message message = new Message("CHUNK",
							messages.get(index).version, server.id,
							messages.get(index).fileId,
							messages.get(index).chunkNr,
							messages.get(index).replicationDegree,
							server.chunks.get(i).data);
>>>>>>> master

					System.out.println("Chunk exists! Fetching it now...");

					server.restoreP.sendQueue.add(message);

				}

			}

		}

	}

	public void processResponses(int i) {

		try {
			newDelay();
			Thread.sleep(randomDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (verifyChunkMessages(messages.get(i).fileId, messages.get(i).chunkNr) == 1) {

			manageGetchunk(i);

		}

	}

	public int verifyChunkMessages(String fileId, int currentChunk) {

		for (int i = 0; i < messages.size(); i++) {

			if (messages.get(i).type.equals("CHUNK")) {

				if (!messages.get(i).senderId.equals(server.id)) {

					if (messages.get(i).fileId.equals(fileId)) {

						if (messages.get(i).chunkNr == currentChunk) {

							return -1;

						}

					}

				}

			}

		}

		return 1;

	}

<<<<<<< HEAD
	public void manageDelete(int index) {

		int i = 0;
		
		while(i < server.chunks.size()) {

			if (messages.get(index).fileId.equals(server.chunks.get(i).identifier)) {

				System.out.println("Found one chunk that belongs to the deleted file! Deleting it now...");

				String newPath = System.getProperty("user.dir") + "\\" + server.chunks.get(i).identifier + "_"
						+ Integer.toString(server.chunks.get(i).index);

				Path path = Paths.get(newPath);

				try {
					Files.deleteIfExists(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				server.chunks.remove(i);

			}else{
				i++;
			}
			
		}

	}

=======
>>>>>>> master
	public void newDelay() {

		Random rand = new Random();

		randomDelay = rand.nextInt((400 - 1) + 1) + 1;

	}

}
