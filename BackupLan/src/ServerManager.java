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

						System.out.println("Found a PUTCHUNK request! Storing chunk now...");
						managePutchunk(i);
						messages.remove(i);

					}
				} else if (messages.get(i).type.equals("GETCHUNK")) {

					if (!messages.get(i).senderId.equals(server.id)) {

						System.out.println("Found a GETCHUNK request! Checking whether the chunk exists or not...");
						processResponses(i);
						messages.remove(i);

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
	
	public void processResponses(int i){
		
		try {
			newDelay();
			Thread.sleep(randomDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(verifyChunkMessages(messages.get(i).fileId, messages.get(i).chunkNr) == 1){
			
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

	public void newDelay() {

		Random rand = new Random();

		randomDelay = rand.nextInt((400 - 1) + 1) + 1;

	}

}
