import java.util.ArrayList;

public class ServerManager implements Runnable{

	public Server server;
	public ArrayList<Message> messages;

	public ServerManager(Server server) {
		messages = new ArrayList<Message>();
		this.server = server;
		startServerManager();

	}
	
	public void startServerManager(){
		
		System.out.println("Server manager kicking off!");
		new Thread(this).start();
		
	}
	
	@Override
	public void run() {
		manageSendingQueues();
	}

	public void manageSendingQueues() {
		
		while (true) {

			for (int i = 0; i < messages.size(); i++) {

				// found a putchunk message so the chunk needs to be created and
				// stored
				if (messages.get(i).type.equals("PUTCHUNK")) {
					
					if(!messages.get(i).senderId.equals(server.id)){

						System.out.println("Found a PUTCHUNK request! Storing chunk now...");
						managePutchunk(i);
						messages.remove(i);
						
					}
				} 
			}
		}

	}

	public void managePutchunk(int i) {

		Chunk chunk = new Chunk(messages.get(i).fileId, messages.get(i).chunkNr,
				messages.get(i).data, messages.get(i).replicationDegree, 1);
		server.chunks.add(chunk);
		chunk.writeChunk();

		Message message = new Message("STORED", messages.get(i).version, server.id,
				messages.get(i).fileId, messages.get(i).chunkNr, 1, null);

		server.controlP.sendQueue.add(message);

	}

}
