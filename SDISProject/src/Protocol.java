import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Protocol {
	// the id of the peer that initiated the protocol
	public int InitiatorId;
	// waiting time for the sender
	public int waitingTime;
	// amount of times the sender has tried to keep the replication degree
	public int nrTries;
	// to save the responses gotten after a backup request
	public ArrayList<String> responses = new ArrayList<String>();
	
	// the protocol constructor
	public Protocol() {
		waitingTime = 60;
		nrTries = 1;
	}

	/* implement protocol for control channel
	 * return syntax:
	 * 	1 -> successful replication degree
	 * 	-1 -> unsuccessful replication degree
	 */
	public int controlProtocol(Server server, String request, int nrChunks) {

		if (request.equals("reply backup")) {
			replyBackup(server, nrChunks);
		} else if (request.equals("listen backup")) {
			if(listenBackup(server) == 1){
				return 1;
			}
			else{
				return -1;
			}
		} else {
			System.out.println("error on the control protocol!");
			System.exit(1);
		}
		
		return 0;

	}

	// implement the protocol for the backup
	public int backupProtocol(Server server, String request) {

		if (request.equals("send")) {
			sendBackup(server);
			return 1;
		} else if (request.equals("listen")) {
			return receiveBackup(server);
		} else {
			System.out.println("error on the backup protocol!");
			System.exit(1);
		}

		return -1;

	}

	// send --> backup protocol
	public void sendBackup(Server server) {

		int nrChunks = 0;
		while (nrChunks < server.serverFile.chunksNo) {
			sendChunk(server, nrChunks);
			nrChunks++;
		}

	}

	// send one chunk
	public void sendChunk(Server server, int nrChunks) {

		// send the chunk
		server.multicast.BackupChannel(server.multicast.backupAddress, server.multicast.backupPort,
				server.serverFile.chunks.get(nrChunks).chunkData, "send");
		System.out.println("--> sent chunk nr " + nrChunks + " from file " + server.serverFile.name);
		
		// get the reply
		if(controlProtocol(server, "listen backup", nrChunks) == -1){
			processRepDegreeFailure(server, nrChunks);
		}
		else if(controlProtocol(server, "listen backup", nrChunks) == 1){
			return;
		}
		else{
			System.out.println("error on the replication degree assurance!");
			System.exit(1);
		}

	}
	
	// processes unsuccesful replication degree
	public void processRepDegreeFailure(Server server, int nrChunks){
		
		// double the waiting time for responses
		waitingTime = waitingTime*2*nrTries;
		
		while(nrTries <= 5){
			nrTries++;
			// try to send chunk again
			sendChunk(server, nrChunks);
		}
		
	}

	/* listen for responses to putchunk request and confirm whether the propagation degree was satisfied or not
	 * 	return syntax:
	 * 		1 successful propragation degree
	 * 		0 unsuccessful propagation degree
	 */
	public int listenBackup(Server server){
		
		String response = null;
		int chunkNr = 0;
		
		long start = System.currentTimeMillis();
		long end = start + waitingTime*1000; // 60 seconds * 1000 ms/sec
		
		while (System.currentTimeMillis() < end){
			System.out.println("Listening for responses to the backup request...");
			response = server.multicast.ControlChannel(server.controlAddress, server.controlPort, null, "listen");
			
			// to find out which peer sent the response
			String[] splitted;
			splitted = response.split(" +");
			String peer = splitted[2];
			chunkNr = Integer.parseInt(splitted[4]);
			
			// save the peer that sent the response
			responses.add(peer);
			System.out.println("Received response!");		
		}
		
		return replicationDegreeTester(server, chunkNr);
		
	}
	
	// test whether the replication Degree was satisfied or not
	public int replicationDegreeTester(Server server, int nrChunk){

		server.serverFile.chunks.get(nrChunk).setActualRep(responses.size());
		differentPeers();
		int actualRepDegree = responses.size();
		
		if(actualRepDegree < server.replicationDegree){
			return 0;
		}
		else{
			return 1;
		}
		
	}
	
	// make sure that the peers are all different
	public void differentPeers(){
		
		// add elements to al, including duplicates
		Set<String> hs = new HashSet<>();
		hs.addAll(responses);
		responses.clear();
		responses.addAll(hs);
		
	}

	// receive --> backup protocol
	public int receiveBackup(Server server) {

		int delay;
		int chunkNr = 0;
		byte[] receivedMessage;
		receivedMessage = server.multicast.BackupChannel(server.controlAddress, server.controlPort, null, "listen");

		try {
			chunkNr = decomposeMessage(receivedMessage);
			delay = randomDelay();
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println(" --- Status Report ---");
		System.out.println("received chunk nr " + chunkNr);

		return chunkNr;

	}

	/*
	 * filter the header from the message return codes: -1 -> erro nr -> chunk
	 * index
	 **/
	public int decomposeMessage(byte[] message) {

		ByteArrayInputStream data = new ByteArrayInputStream(message);
		byte[] chunkData = null;
		byte[] header = null;

		for (int i = 0; i < message.length; i++) {

			if (Byte.toString(message[i]).equals("LF")) {
				data.read(header, 0, i + 2);
				data.read(chunkData, i + 3, message.length);
			}

		}

		// split the information on the received request
		try {
			String headerString = new String(header, "UTF-8");
			String[] splitted;

			splitted = headerString.split(" +");

			// PUTCHUNK message
			if (splitted[0].equals("PUTCHUNK")) {

				Chunk chunk = new Chunk(splitted[3], Integer.parseInt(splitted[4]), chunkData,
						Integer.parseInt(splitted[5]), 0);
				chunk.writeChunk(chunk);
				return Integer.parseInt(splitted[4]);

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return -1;

	}

	// send a message saying the chunk has been stored
	public String replyBackup(Server server, int chunkNo) {

		String status;

		StringBuilder nrChunk = new StringBuilder();
		nrChunk.append(chunkNo);

		String reply = "STORED" + " " + "1.0" + " " + server.id + " " + server.serverFile.identifier + " "
				+ nrChunk.toString() + " " + "CRLF" + "CRLF";

		status = server.multicast.ControlChannel(server.multicast.controlAddress, server.multicast.controlPort, reply,
				"send");

		return status;

	}

	// gets the random delay
	@SuppressWarnings("null")
	public int randomDelay() {

		Random random = null;

		return random.nextInt((400 - 0) + 1) + 0;

	}

}
