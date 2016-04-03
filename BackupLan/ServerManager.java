import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class ServerManager implements Runnable {

	public Server server;
	public ArrayList<Message> messages;
	public ArrayList<Chunk> chunksToUpdate;

	public int randomDelay;

	public ServerManager(Server server) {
		messages = new ArrayList<Message>();
		chunksToUpdate = new ArrayList<Chunk>();

		this.server = server;
		startServerManager();

	}

	public void startServerManager() {

		System.out.println("Setting up the Server Manager");
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

						if (isNotForbiddenChunk(i) == 1) {

							System.out.println("Found a PUTCHUNK request! Storing chunk now...");
							managePutchunk(i);
							messages.remove(i);

						}

					}
				} else if (messages.get(i).type.equals("GETCHUNK")) {

					if (!messages.get(i).senderId.equals(server.id)) {

						try {
							Thread.sleep(800);
						} catch (Exception e) {
							e.printStackTrace();
						}

						System.out.println("Found a GETCHUNK request! Checking whether the chunk exists or not...");
						processResponses(i);
						messages.remove(i);

					}

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

					}
				} else if (messages.get(i).type.equals("REMOVED")) {

					if (!messages.get(i).senderId.equals(server.id)) {

						try {
							Thread.sleep(400);
						} catch (Exception e) {
							e.printStackTrace();
						}

						System.out.println("Found a REMOVED message! Decrementing chunk count...");
						if (manageRemoved(i) == 1)
							System.out.println("Removed chunks...");
						else
							System.out.println("Failed to remove chunks...");

						try {
							Thread.sleep(randomDelay);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (findPutchunk(i) != 1) {

							Chunk chunk = verifyRepDegree(i);

							// replication degree below
							if (chunk != null) {

								Message m = new Message("PUTCHUNK", "1.0", server.id, messages.get(i).fileId,
										messages.get(i).chunkNr, chunk.replicationDegree, chunk.data);

								server.putchunkRequests.add(m);
								System.out.println("Added request");

							}

						} else {
							System.out.println("Backup subprotocol already initiated by another peer...");
						}

						messages.remove(i);
						System.out.println("Finished server manager removed");

					}

					newDelay();

				}

				if (server.backupEnh)
					updateChunkCount();

			}

			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void managePutchunk(int i) {

		Chunk chunk = new Chunk(messages.get(i).fileId, messages.get(i).chunkNr, messages.get(i).data,
				messages.get(i).replicationDegree, 1);
		server.chunks.add(chunk);
		chunk.writeChunk();

		Message message = new Message("STORED", messages.get(i).version, server.id, messages.get(i).fileId,
				messages.get(i).chunkNr, 1, null);

		server.controlP.sendQueue.add(message);

	}

	public void manageGetchunk(int index) {

		for (int i = 0; i < server.chunks.size(); i++) {

			if (messages.get(index).fileId.equals(server.chunks.get(i).identifier)) {

				if (messages.get(index).chunkNr == server.chunks.get(i).index) {

					Message message = new Message("CHUNK", messages.get(index).version, server.id,
							messages.get(index).fileId, messages.get(index).chunkNr,
							messages.get(index).replicationDegree, server.chunks.get(i).data);

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

	public void manageDelete(int index) {

		int i = 0;

		while (i < server.chunks.size()) {

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

			} else {
				i++;
			}

		}

	}

	public int manageRemoved(int index) {

		for (int i = 0; i < server.chunks.size(); i++) {

			if (server.chunks.get(i).identifier.equals(messages.get(index).fileId)) {

				if (server.chunks.get(i).index == messages.get(index).chunkNr) {

					server.chunks.get(i).decrementActualDeg();
					System.out.println("Chunk " + server.chunks.get(i).identifier + "_" + server.chunks.get(i).index
							+ " -*- " + server.chunks.get(i).actualRepDeg);
					return 1;

				}

			}

		}

		for (int j = 0; j < server.files.size(); j++) {

			if (server.files.get(j).identifier.equals(messages.get(index).fileId)) {

				for (int k = 0; k < server.files.get(j).chunks.size(); k++) {

					if (server.files.get(j).chunks.get(k).index == messages.get(index).chunkNr) {

						server.files.get(j).chunks.get(k).decrementActualDeg();
						System.out.println("Chunk " + server.files.get(j).chunks.get(k).identifier + "_"
								+ server.files.get(j).chunks.get(k).index + " - "
								+ server.files.get(j).chunks.get(k).actualRepDeg);
						return 1;

					}
				}

			}

		}

		return 0;

	}

	public Chunk verifyRepDegree(int index) {

		for (int i = 0; i < server.chunks.size(); i++) {

			if (server.chunks.get(i).identifier.equals(messages.get(index).fileId)
					&& server.chunks.get(i).index == messages.get(index).chunkNr) {

				if (server.chunks.get(i).actualRepDeg < server.chunks.get(i).replicationDegree) {

					System.out.println("Found chunk that droped below the desired replication degree");
					System.out.println(server.chunks.get(i).identifier + " *-* " + server.chunks.get(i).index);
					return server.chunks.get(i);

				}
			}

		}

		for (int k = 0; k < server.files.size(); k++) {

			if (server.files.get(k).identifier.equals(messages.get(index).fileId)) {

				for (int j = 0; j < server.files.get(k).chunks.size(); j++) {

					if (server.files.get(k).chunks.get(j).index == messages.get(index).chunkNr) {

						if (server.files.get(k).chunks.get(j).actualRepDeg < server.files.get(k).chunks
								.get(j).replicationDegree) {

							System.out.println("Found chunk that droped below the desired replication degree");
							System.out.println(
									server.files.get(k).identifier + " - " + server.files.get(k).chunks.get(j).index);
							return server.files.get(k).chunks.get(j);

						}
					}

				}
			}

		}

		return null;

	}

	public int findPutchunk(int index) {

		for (int i = index + 1; i < messages.size(); i++) {

			if (messages.get(i).type.equals("PUTCHUNK")) {

				if (messages.get(i).fileId.equals(messages.get(index).fileId)) {

					if (messages.get(i).chunkNr == messages.get(index).chunkNr) {

						return 1;

					}

				}

			}

		}

		return 0;

	}

	public int isNotForbiddenChunk(int index) {

		for (int i = 0; i < server.forbiddenChunks.size(); i++) {

			if (server.forbiddenChunks.get(i).identifier.equals(messages.get(index).fileId)) {

				if (server.forbiddenChunks.get(i).index == messages.get(index).chunkNr) {

					// it's a forbidden chunk
					return 0;

				}

			}

		}

		// it's not a forbidden chunk
		return 1;

	}

	public void updateChunkCount() {

		for (int i = 0; i < chunksToUpdate.size(); i++) {

			int chunkIndex = findUpdateChunk(i);
			if (chunkIndex != -1)
				server.chunks.get(chunkIndex).incrementActualDeg();
			else {

				int existentChunk = findExistentChunk(i);

				if (existentChunk != 1)
					server.existentChunks.get(existentChunk).incrementActualDeg();
				else {

					Chunk chunk = new Chunk(chunksToUpdate.get(i).identifier, chunksToUpdate.get(i).index, null,
							chunksToUpdate.get(i).replicationDegree, 1);
					server.existentChunks.add(chunk);
				}

			}

		}

		verifyForbidden();

	}

	public void verifyForbidden() {

		for (int i = 0; i < server.chunks.size(); i++) {

			if (server.chunks.get(i).actualRepDeg >= server.chunks.get(i).replicationDegree) {

				server.forbiddenChunks.add(server.chunks.get(i));

			}

		}

		for (int k = 0; k < server.existentChunks.size(); k++) {

			if (server.existentChunks.get(k).actualRepDeg >= server.existentChunks.get(k).replicationDegree) {

				server.forbiddenChunks.add(server.chunks.get(k));

			}

		}

	}

	public int findUpdateChunk(int index) {

		for (int i = 0; i < server.chunks.size(); i++) {

			if (server.chunks.get(i).identifier.equals(chunksToUpdate.get(index).identifier)
					&& server.chunks.get(i).index == chunksToUpdate.get(index).index) {

				return i;

			}

		}

		return -1;

	}

	public int findExistentChunk(int index) {

		for (int i = 0; i < server.existentChunks.size(); i++) {

			if (server.existentChunks.get(i).identifier.equals(chunksToUpdate.get(index).identifier)
					&& server.existentChunks.get(i).index == chunksToUpdate.get(index).index) {

				return i;

			}

		}

		return -1;

	}

	public void newDelay() {

		Random rand = new Random();

		randomDelay = rand.nextInt((400 - 1) + 1) + 1;

	}

}
