import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;

public class ProcessControl implements Runnable {

	public Server server;
	public ArrayList<Message> sendQueue = new ArrayList<Message>();
	public int randomDelay;

	public ProcessControl(Server server) {
		this.server = server;

		processControlQueue();

	}

	public void processControlQueue() {

		System.out.println("Control processor is kicking off");
		new Thread(this).start();

	}

	@Override
	public void run() {

		DatagramPacket toSend;
		byte[] buffer;
		String message;

		// send the messages on the sendQueue
		while (true) {

			newDelay();

			for (int i = 0; i < sendQueue.size(); i++) {

				if (sendQueue.get(i).type.equals("STORED")) {

					message = "STORED" + " " + sendQueue.get(i).version + " " + sendQueue.get(i).senderId + " "
							+ sendQueue.get(i).fileId + " " + sendQueue.get(i).chunkNr + " " + "\r\n" + "\r\n";

					try {

						buffer = message.getBytes();

						toSend = new DatagramPacket(buffer, buffer.length, server.multicast.controlIP,
								server.multicast.controlPort);

						server.multicast.controlSocket.send(toSend);

						System.out.println("Sent stored message");

					} catch (IOException e) {
						e.printStackTrace();
					}

					sendQueue.remove(i);

				}

			}

			try {
				Thread.sleep(randomDelay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void newDelay() {

		Random rand = new Random();

		randomDelay = rand.nextInt((400 - 1) + 1) + 1;

	}

}
