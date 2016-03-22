import java.io.IOException;
import java.net.DatagramPacket;

public class CounterControl implements Runnable {

	public Server server;
	public ServerManager serverManager;

	public CounterControl(Server server, ServerManager serverManager) {

		this.server = server;
		this.serverManager = serverManager;
		createCounterControl();

	}

	public void createCounterControl() {

		System.out.println("Setting up the Control Counter");
		new Thread(this).start();

	}

	@Override
	public void run() {

		byte[] buffer;
		DatagramPacket toReceive;

		while (true) {

			buffer = new byte[256];
			toReceive = new DatagramPacket(buffer, buffer.length, server.multicast.controlIP,
					server.multicast.controlPort);

			try {
				server.multicast.controlSocket.receive(toReceive);

				String received = new String(toReceive.getData()).substring(0, toReceive.getLength());

				Message message = parseMessage(received);

				if (message != null) {
					System.out.println("Control Counter has received a message of the type " + message.type);
					serverManager.messages.add(message);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public Message parseMessage(String message) {

		Message m;
		String[] messageSplit;
		messageSplit = message.split(" +");

		if (messageSplit[0].equals("STORED") || messageSplit[0].equals("GETCHUNK")
				|| messageSplit[0].equals("REMOVED")) {

			m = new Message(messageSplit[0], messageSplit[1], messageSplit[2], messageSplit[3],
					Integer.parseInt(messageSplit[4]), -1, null);

		} else if (messageSplit[0].equals("DELETE")) {

			m = new Message(messageSplit[0], messageSplit[1], messageSplit[2], messageSplit[3], -1, -1, null);

		} else {
			System.out.println("Received message that is not supported by the system.... Bye....");
			m = null;
		}

		return m;

	}

}
