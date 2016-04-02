import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;

public class ProcessRestore implements Runnable {

	public Server server;

	public int randomDelay;

	public ArrayList<Message> sendQueue = new ArrayList<Message>();

	public ProcessRestore(Server server) {
		this.server = server;

		processRestore();
	}

	public void processRestore() {

		System.out.println("Setting up the Restore Processor");
		new Thread(this).start();

	}

	@Override
	public void run() {

		while (true) {

			newDelay();

			try {
				Thread.sleep(randomDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < sendQueue.size(); i++) {

				if (sendQueue.get(i).type.equals("CHUNK")) {

					Chunk toSend = getChunk(i);

					if (toSend == null) {
						System.out.println("Chunk was not found!");
					} else {

						sendChunk(toSend, sendQueue.get(i));
						sendQueue.remove(i);

					}

				}

			}

		}

	}

	public Chunk getChunk(int index) {

		for (int i = 0; i < server.chunks.size(); i++) {

			if (server.chunks.get(i).identifier.equals(sendQueue.get(index).fileId)) {

				if (server.chunks.get(i).index == sendQueue.get(index).chunkNr) {

					return server.chunks.get(i);

				}

			}

		}

		return null;

	}

	public void sendChunk(Chunk chunk, Message message) {
		String header = message.type + " " + message.version + " " + server.id + " " + message.fileId + " "
				+ message.chunkNr + " " + "\r\n" + "\r\n";

		byte[] head = new byte[35];
		head = header.getBytes();

		System.out.println("Restore chunk header formed");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			outputStream.write(head);
			outputStream.write(message.data);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		byte[] buffer = outputStream.toByteArray();

		DatagramPacket toSend = new DatagramPacket(buffer, buffer.length, server.multicast.restoreIP,
				server.multicast.restorePort);

		try {
			server.multicast.restoreSocket.send(toSend);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Sent restore chunk nr " + message.chunkNr);

	}

	public void newDelay() {

		Random rand = new Random();

		randomDelay = rand.nextInt((400 - 1) + 1) + 1;

	}

}
