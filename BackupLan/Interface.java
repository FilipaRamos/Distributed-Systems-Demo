import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Interface {

	DatagramSocket serverSocket;

	public Server server;

	public Interface(Server server) {
		this.server = server;
	}

	public void getCommand() {

		try {

			serverSocket = new DatagramSocket(9568);

			byte[] received = new byte[1024];

			DatagramPacket receivePacket = new DatagramPacket(received, received.length);
			serverSocket.receive(receivePacket);

			System.out.println("RECEIVED");

			String receivedInfo = new String(receivePacket.getData());

			System.out.println("Received from the client the request " + receivedInfo);

			parseRequest(receivedInfo);

		} catch (Exception e) {
		}

	}

	public void parseRequest(String received) {

		System.out.println(received);

		String[] splitted;
		splitted = received.split("\\s+");

		if (splitted[0].equals("BACKUP")) {

			server.id = splitted[1];

			Request request = new Request("BACKUP", splitted[2], Integer.parseInt(splitted[3]), 0, false);
			server.requests.add(request);

		} else if (splitted[0].equals("BACKUPENH")) {

			server.id = splitted[1];

			Request request = new Request("BACKUP", splitted[2], Integer.parseInt(splitted[3]), 0, true);
			server.requests.add(request);

		} else if (splitted[0].equals("RESTORE")) {

			server.id = splitted[1];

			Request request = new Request("RESTORE", splitted[2], 0, 0, false);
			server.requests.add(request);

		} else if (splitted[0].equals("DELETE")) {

			server.id = splitted[1];

			Request request = new Request("DELETE", splitted[2], 0, 0, false);
			server.requests.add(request);

		} else if (splitted[0].equals("RECLAIM")) {

			server.id = splitted[1];

			Request request = new Request("RECLAIM", null, 0, Integer.parseInt(splitted[2]), false);
			server.requests.add(request);

		}

		System.out.println("Message from client parsed");

	}

}
