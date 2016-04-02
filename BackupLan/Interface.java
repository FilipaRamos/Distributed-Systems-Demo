import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Interface {

	ServerSocket serverSocket;

	public Server server;

	public Interface(Server server) {
		this.server = server;
	}

	public void getCommand() {

		try {

			serverSocket = new ServerSocket(9568);

			Socket connectionSocket = serverSocket.accept();

			BufferedReader inFromClient = new BufferedReader
					(new InputStreamReader(connectionSocket.getInputStream()));  
			
			String clientRequest = inFromClient.readLine();   
			
			System.out.println("Received from the client the request " + clientRequest);

			parseRequest(clientRequest);

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
