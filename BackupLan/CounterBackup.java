import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.util.Arrays;

public class CounterBackup implements Runnable {

	public Server server;
	public ServerManager serverManager;

	public CounterBackup(Server server, ServerManager serverManager) {

		this.server = server;
		this.serverManager = serverManager;
		createCounterBackup();

	}

	public void createCounterBackup() {

		System.out.println("Setting up the Backup Counter");
		new Thread(this).start();

	}

	@Override
	public void run() {

		byte[] buffer;
		DatagramPacket toReceive;

		while (true) {

			try {

				buffer = new byte[2 << 16];
				toReceive = new DatagramPacket(buffer, buffer.length);

				server.multicast.backupSocket.receive(toReceive);

				Message message = parseMessage(toReceive.getData(), toReceive.getLength());
				
				if (message != null) {
					System.out.println("Backup Counter has received a message of the type " + message.type + " "
							+ message.senderId);
					serverManager.messages.add(message);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public Message parseMessage(byte[] message, int length) throws UnsupportedEncodingException {

		Message m;

		byte[] chunkData = null;
		byte[] header = null;

		for (int i = 0; i < length - 1; i++) {

			if (message[i] == 0xd && message[i + 1] == 0xa) {

				if (message[i + 2] == 0xd && message[i + 3] == 0xa) {
					
					header = new byte[i + 3];
					chunkData = new byte[length - (i + 3)];
					
					header = Arrays.copyOfRange(message, 0, i+3);
					chunkData = Arrays.copyOfRange(message, i+4, length);
					break;
					
				}
			}

		}
		
		// split the information on the received request
		String headerString = new String(header, "UTF-8");
		String[] messageSplit;

		messageSplit = headerString.split("\\s+");

		if (messageSplit[0].equals("PUTCHUNK")) {

			m = new Message(messageSplit[0], messageSplit[1], messageSplit[2], messageSplit[3],
					Integer.parseInt(messageSplit[4]), Integer.parseInt(messageSplit[5]), chunkData);

		} else {
			System.out.println("Received message that is not supported by the system.... Bye....");
			m = null;
		}

		return m;

	}

}
