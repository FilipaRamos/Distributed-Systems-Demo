import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class ProcessBackup implements Runnable {

	public Server server;
	public ServerManager serverManager;
	public FileEvent fileEvent;
	public ArrayList<String> receivedStored = new ArrayList<String>();

	public int waitingTime;
	public int nrTries;

	public ProcessBackup(Server server, ServerManager serverManager, FileEvent fileEvent) {
		this.server = server;
		this.serverManager = serverManager;
		this.fileEvent = fileEvent;
		
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

		int nrChunks = 0;
		while (nrChunks < fileEvent.chunksNo) {
			if(sendChunk(server, server.requests.get(nrChunks)) == 1){
				System.out.println("Processed chunk nr " + nrChunks);
				nrChunks++;
				receivedStored.clear();
				waitingTime = 1000;
				nrTries = 0;
			}
			if(nrTries >= 5){
				nrChunks++;
			}
		}

		server.requests.clear();

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
			fileEvent.chunks.get(message.chunkNr).actualRepDeg = receivedStored.size();
			return -1;

		} else {

			System.out.println("Desired replication degree was achieved!");
			fileEvent.chunks.get(message.chunkNr).actualRepDeg = receivedStored.size();
			return 1;

		}

	}

}
