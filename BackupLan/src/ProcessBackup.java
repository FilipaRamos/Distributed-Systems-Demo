import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class ProcessBackup implements Runnable {

	public Server server;
	public ArrayList<String> receivedStored = new ArrayList<String>();

	public int waitingTime;
	public int nrTries;

	public ProcessBackup(Server server) {
		this.server = server;
		this.waitingTime = 1000;
		this.nrTries = 0;
	}

	public void processControlQueue() {

		Thread processBackup = new Thread();
		processBackup.start();

	}

	public void processRequest() {

		int nrChunks = 0;
		while (nrChunks < server.fileEvent.chunksNo) {
			sendChunk(server, server.requests.get(nrChunks));
			nrChunks++;
		}

	}

	public void sendChunk(Server server, Message request) {

		String header = request.type + " " + request.version + " " + server.id + " " + request.fileId + " "
				+ request.chunkNr + " " + request.replicationDegree + " " + "\r\n\r\n";

		byte[] head = new byte[25];
		head = header.getBytes();

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

		verifyRepDeg(request);
		
		receivedStored.clear();

	}

	public void verifyRepDeg(Message message) {
		
		while (nrTries < 5) {
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < server.messages.size(); i++) {

				if (server.messages.get(i).type.equals("STORED")) {

					if (server.messages.get(i).fileId.equals(message.fileId)
							&& server.messages.get(i).chunkNr == message.chunkNr) {

						if (!receivedStored.contains(server.messages.get(i).senderId) || server.messages.get(i).senderId != server.id) {

							System.out.println("Received one confirmation that the chunk was stored.");
							receivedStored.add(server.messages.get(i).senderId);

						}

					}

				}

			}

			if (receivedStored.size() < message.replicationDegree) {

				System.out.println("Desired replication degree was not achieved! Trying again...");
				nrTries++;
				waitingTime = waitingTime + 2*nrTries;
				server.fileEvent.chunks.get(message.chunkNr).actualRepDeg = receivedStored.size();
				
			}
			else{
				System.out.println("Desired replication degree was achieved!");
				server.fileEvent.chunks.get(message.chunkNr).actualRepDeg = receivedStored.size();
				break;
			}
		}

	}

	@Override
	public void run() {

		processRequest();

	}

}
