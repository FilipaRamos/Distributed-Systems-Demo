import java.io.File;
import java.net.SocketException;

public class ThreadEngine implements Runnable {
	// the operation to perform
	public String operation;
	// the server object
	public Server server;
	// the file to operate
	public ServerFile file;
	public File f;
	// the multicast object
	public Multicast multicast;
	// response received on the control channel
	public String controlResponse;
	// response received on the backup channel
	public String backupResponse;
	// the backup protocol
	public Protocol backupProtocol = new Protocol();
	// the index of the chunk
	public int chunkNr = 0;

	// constructor for the ThreadEngine
	public ThreadEngine(String operation, Server server, ServerFile file, File f) {
		this.operation = operation;
		this.server = server;
		this.file = file;
		this.f = f;
		this.multicast = server.multicast;
	}

	// create thread
	public void CreateThread(ThreadEngine engine) {
		new Thread(engine).start();
	}

	// kill thread
	public void KillThread() {}

	@Override
	public void run() {

		switch (operation) {
		case "listen":
			while (true) {
				try {
					controlResponse = multicast.ControlChannel(multicast.controlAddress, multicast.controlPort, " ",
							"listen");
					System.out.println(controlResponse);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		case "request":
			while (true) {
				try {
					controlResponse = multicast.ControlChannel(multicast.controlAddress, multicast.controlPort, "Olá",
							"send");
					System.out.println(controlResponse);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		case "backup":
			try {
				backupProtocol.backupProtocol(server, "send");
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "listen backup":
			int indexChunk = 0;
			while(true){
				try {
					indexChunk = backupProtocol.backupProtocol(server, "listen");
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					backupProtocol.controlProtocol(server, "reply backup", indexChunk);
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Replied after recieving and storing a chunk!");
			}
		case "restore":
			break;
		case "delete":
			break;
		case "reclaim":
			break;
		default:
			break;

		}

	}

}
