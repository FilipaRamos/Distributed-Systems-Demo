
public class ServerManager implements Runnable{

	public Server server;

	public ServerManager(Server server) {

		this.server = server;

	}
	
	public void startServerManager(){
		
		System.out.println("Server manager kicking off!");
		new Thread(this).start();
		
	}

	public void manageSendingQueues() {

		while (true) {

			for (int i = 0; i < server.messages.size(); i++) {

				// found a putchunk message so the chunk needs to be created and
				// stored
				if (server.messages.get(i).type.equals("PUTCHUNK")) {

					System.out.println("Found a PUTCHUNK request! Storing chunk now...");
					managePutchunk(i);

				} else if (server.messages.get(i).type.equals("GETCHUNK")) {

					// mdr send queue

				} else if (server.messages.get(i).type.equals("CHUNK")) {

					// mdr chunk history

				}

			}

		}

	}

	public void managePutchunk(int i) {

		Chunk chunk = new Chunk(server.messages.get(i).fileId, server.messages.get(i).chunkNr,
				server.messages.get(i).data, server.messages.get(i).replicationDegree, 1);
		server.chunks.add(chunk);
		chunk.writeChunk();

		Message message = new Message("STORED", server.messages.get(i).version, server.id,
				server.messages.get(i).fileId, server.messages.get(i).chunkNr, 1, null);

		server.controlP.sendQueue.add(message);

	}

	@Override
	public void run() {
		manageSendingQueues();
	}

}
