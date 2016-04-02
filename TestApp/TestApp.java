import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestApp {
	// IP address and Port number used by the initiator
	public String peer;

	// BACKUP / RESTORE / DELETE / RECLAIM
	public String protocol;

	// Path name or Amount of space to reclaim
	public String opnd_1;

	// Replication degree
	public int rep;

	public TestApp() {
	}

	public static void main(String args[]) throws NumberFormatException, UnknownHostException, IOException {
		if (args.length < 4 || args.length > 5) {
			System.out.println("Wrong number of arguments!");
		}

		TestApp app = new TestApp();
		DatagramSocket appSocket = new DatagramSocket();
		InetAddress IPAddress;

		app.protocol = args[1];
		app.opnd_1 = args[2];
		app.rep = Integer.parseInt(args[3]);

		if (args[0].indexOf(":") != -1 && args[0].indexOf(":") != 0) {

			String[] splitted;
			splitted = (app.peer).split(":");

			IPAddress = InetAddress.getByName("localhost");
			app.peer = splitted[1];

		} else if (args[0].indexOf(":") == 0) {

			String splitted = args[0].substring(1);

			IPAddress = InetAddress.getByName("localhost");
			app.peer = splitted;

		} else {

			IPAddress = InetAddress.getByName(args[0]);
			app.peer = args[0];

		}

		System.out.println("Estabilished communication with initiator peer");

		String toSend = null;

		if (app.protocol.equals("BACKUP") || app.protocol.equals("BACKUPENH")) {
			// BACKUP serverId path repDegree
			toSend = app.protocol + " " + app.peer + " " + args[2] + " " + args[3];
		} else if (app.protocol.equals("RESTORE")) {
			// RESTORE serverOd
			toSend = "RESTORE " + app.peer + " " + args[2];
		} else if (app.protocol.equals("DELETE")) {
			toSend = "DELETE " + app.peer + " " + args[2];
		} else if (app.protocol.equals("RECLAIM")) {
			toSend = "RECLAIM " + app.peer + " " + args[2];
		} else {
			System.out.println("ERROR! Command not supported!");
			System.exit(0);
		}

		byte[] sendData = new byte[1024];
		sendData = toSend.getBytes();

		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,
				Integer.parseInt(app.peer));
		appSocket.send(sendPacket);
		
		System.out.println("Sent");

		appSocket.close();

	}
}