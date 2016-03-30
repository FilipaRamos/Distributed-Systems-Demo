import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class ProcessBackup implements Runnable {

	public Server server;
	public ServerManager serverManager;
	public ArrayList<String> receivedStored = new ArrayList<String>();

	public int waitingTime;
	public int nrTries;

	public ProcessBackup(Server server, ServerManager serverManager) {
		this.server = server;
		this.serverManager = serverManager;

		waitingTime = 1000;
		nrTries = 0;

		processBackup();

	}

	public void processBackup() {

		System.out.println("Lauching the backup processor to process a PUTCHUNK request");
		new Thread(this).start();

	}

	@Override
	public void run() {

		processRequest();

	}

	public void processRequest() {

		while (true) {

			int nrChunks = 0;
			while (nrChunks < server.requests.size()) {
				if (sendChunk(server, server.requests.get(nrChunks)) == 1) {
					System.out.println("Processed chunk nr " + nrChunks);
					nrChunks++;
					receivedStored.clear();
					waitingTime = 1000;
					nrTries = 0;
				}
				if (nrTries >= 5) {
					System.out.println("It wasn't possible to store the chunk with the desired replication degree :(");
					nrChunks++;
				}
			}

			server.requests.clear();
			
		}

	}

	public int sendChunk(Server server, Message request) {

		String header = request.type + " " + request.version + " " + server.id + " " + request.fileId + " "
				+ request.chunkNr + " " + request.replicationDegree + " " + "\r\n" + "\r\n";

		byte[] head = new byte[50];
		head = header.getBytes();

		System.out.println("Chunk header formed");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			outputStream.write(head);
			outputStream.write(request.data);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		byte[] buffer = outputStream.toByteArray();
		DatagramPacket toSend = new DatagramPacket(buffer, buffer.length, server.multicast.backupIP,
				server.multicast.backupPort);

		try {
			server.multicast.backupSocket.send(toSend);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Sent chunk nr " + request.chunkNr);

		return verifyRepDeg(request);

	}

	public int verifyRepDeg(Message message) {

		try {
			Thread.sleep(waitingTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < serverManager.messages.size(); i++) {

			if (serverManager.messages.get(i).type.equals("STORED")) {

				if (serverManager.messages.get(i).fileId.equals(message.fileId)
						&& serverManager.messages.get(i).chunkNr == message.chunkNr) {

					if (!(receivedStored.contains(serverManager.messages.get(i).senderId))
							&& !(serverManager.messages.get(i).senderId.equals(server.id))) {

						System.out.println("Received one confirmation that the chunk was stored.");
						receivedStored.add(serverManager.messages.get(i).senderId);

					}

				}

			}

		}

		if (receivedStored.size() < message.replicationDegree) {

			System.out.println("Desired replication degree was not achieved! Trying again...");
			nrTries++;
			waitingTime = waitingTime * 2 * nrTries;
			findFile(message).chunks.get(message.chunkNr).actualRepDeg = receivedStored.size();
			return -1;

		} else {

			System.out.println("Desired replication degree was achieved!");
			findFile(message).chunks.get(message.chunkNr).actualRepDeg = receivedStored.size();
			return 1;

		}

	}

	public void reclaimBelow() {

		try {
			Thread.sleep(waitingTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int dif = 0;

		for (int i = 0; i < server.belowChunks.size(); i++) {

			dif = server.belowChunks.get(i).replicationDegree - server.belowChunks.get(i).actualRepDeg;

			Message message = new Message("PUTCHUNK", "1.0", server.id, server.belowChunks.get(i).identifier,
					server.belowChunks.get(i).index, dif, server.belowChunks.get(i).data);

			while (nrTries < 5) {
				if (sendChunk(server, message) == 1) {
					System.out.println("Starting backup subprotocol for chunk " + server.belowChunks.get(i).identifier
							+ "_" + server.belowChunks.get(i).index);
					receivedStored.clear();
					waitingTime = 1000;
					nrTries = 0;
					break;
				}

			}

		}
	}

	public FileEvent findFile(Message message) {

		for (int i = 0; i < server.files.size(); i++) {

			if (server.files.get(i).identifier.equals(message.fileId)) {

				return server.files.get(i);

			}

		}

		return null;

	}

}
