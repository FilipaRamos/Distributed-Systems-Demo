import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;

public class ProcessControl implements Runnable {

	public Server server;
	public ArrayList<Message> sendQueue = new ArrayList<Message>();

	public ProcessControl(Server server) {
		this.server = server;
		
		processControlQueue();
		
	}

	public void processControlQueue() {

		System.out.println("Backup Counter has received a message of the type ");
		Thread processControl = new Thread();
		processControl.start();

	}

	@Override
	public void run() {
		
		DatagramPacket toSend;
		byte[] buffer;
		String message;

		// send the messages on the sendQueue
		while (true) {

			for (int i = 0; i < sendQueue.size(); i++) {

				if (sendQueue.get(i).type.equals("STORED")) {

					message = "STORED" + " " + sendQueue.get(i).version + " " + sendQueue.get(i).senderId + " "
							+ sendQueue.get(i).fileId + " " + sendQueue.get(i).chunkNr + " " + "\r\n" + "\r\n";
					
					try{
						buffer = message.getBytes();
					
						toSend = new DatagramPacket(buffer, buffer.length, server.multicast.controlIP, server.multicast.controlPort);
					
						server.multicast.controlSocket.send(toSend);
						
						System.out.println("Sent stored message");
						
					}catch(IOException e){
						e.printStackTrace();
					}

				}

			}

		}

	}

}
