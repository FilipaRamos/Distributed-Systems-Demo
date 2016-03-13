import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
	public void KillThread() {

	}

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
			int nrChunks = 0;
			while (nrChunks < file.chunksNo) {
				try {
					multicast.BackupChannel(multicast.backupAddress, multicast.backupPort,
							file.chunks.get(nrChunks).chunkData, "send");
					System.out.print(" sent ");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nrChunks++;
			}
			break;
		case "listen backup":
			int chunkIndex = 0;
			while (true) {
				try {
					System.out.print("estou aqui");
					byte[] received;
					received = multicast.BackupChannel(multicast.backupAddress, multicast.backupPort, null, "listen");
					System.out.println("received chunk");
					Chunk chunk = new Chunk(file.identifier, file.name, chunkIndex, received);
					chunk.writeChunk(chunk);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chunkIndex++;
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
