import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;

public class CounterRestore implements Runnable {

	public Server server;
	public ServerManager serverManager;

	public CounterRestore(Server server, ServerManager serverManager) {

		this.server = server;
		this.serverManager = serverManager;
		createCounterRestore();

	}

	public void createCounterRestore() {

		System.out.println("Setting up the Restore Counter");
		new Thread(this).start();

	}

	@Override
	public void run() {

		byte[] buffer;
		DatagramPacket toReceive;

		while (true) {

			buffer = new byte[2 << 16];
			toReceive = new DatagramPacket(buffer, buffer.length, server.multicast.restoreIP,
					server.multicast.restorePort);

			try {
				server.multicast.restoreSocket.receive(toReceive);

<<<<<<< HEAD
				if (parseMessage(toReceive.getData(), toReceive.getLength()) == 1) {

					System.out.println("Restore Counter has received a message of the type CHUNK");

				}

			} catch (IOException e) {

=======
				if(parseMessage(toReceive.getData()) == 1){

					System.out.println("Restore Counter has received a message of the type CHUNK");
				
				}

			} catch (IOException e) {
				
>>>>>>> master
			}

		}

	}

<<<<<<< HEAD
	public int parseMessage(byte[] message, int length) throws UnsupportedEncodingException {
=======
	public int parseMessage(byte[] message) throws UnsupportedEncodingException {
>>>>>>> master

		ByteArrayInputStream data = new ByteArrayInputStream(message);
		byte[] chunkData = null;
		byte[] header = null;

<<<<<<< HEAD
		for (int i = 0; i < length - 1; i++) {
=======
		for (int i = 0; i < message.length - 1; i++) {
>>>>>>> master

			if (message[i] == 0xd && message[i + 1] == 0xa) {

				if (message[i + 2] == 0xd && message[i + 3] == 0xa) {

					header = new byte[i + 3];
<<<<<<< HEAD
					chunkData = new byte[length - (i + 3)];

					data.read(header, 0, i + 3);
					data.read(chunkData, 0, length - (i + 3));
=======
					chunkData = new byte[message.length - (i + 1)];

					data.read(header, 0, i + 3);
					data.read(chunkData, 0, message.length - (i + 1));
>>>>>>> master
					break;
				}
			}

		}

		// split the information on the received request
		String headerString = new String(header, "UTF-8");
		String[] messageSplit;

		messageSplit = headerString.split(" +");

		if (messageSplit[0].equals("CHUNK")) {

<<<<<<< HEAD
			if (server.file != null) {

				if (messageSplit[3].equals(server.file.identifier) && (!messageSplit[2].equals(server.id))) {

					Chunk chunk = new Chunk(server.file.identifier, Integer.parseInt(messageSplit[4]), chunkData, 1, 1);
					server.file.chunks.add(chunk);
					return 1;

				}
			}

=======
			if (messageSplit[3].equals(server.file.identifier) && !messageSplit[2].equals(server.id)) {

				Chunk chunk = new Chunk(server.file.identifier, Integer.parseInt(messageSplit[4]), chunkData, 1, 1);
				server.file.chunks.add(chunk);
				return 1;
				
			}
			
>>>>>>> master
			return -1;

		} else {
			System.out.println("Received message that is not supported by the system.... Bye....");
			return -1;
		}

	}

}
