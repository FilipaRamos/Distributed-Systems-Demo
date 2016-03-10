public class ThreadEngine implements Runnable {
	// the operation to perform
	public String operation;
	// the multicast object
	public Multicast multicast;
	// response received
	public String response;

	// constructor for the ThreadEngine
	public ThreadEngine(String operation, Multicast multicast) {
		this.operation = operation;
		this.multicast = multicast;
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
					response = multicast.ControlChannel(multicast.controlAddress, multicast.controlPort, " ", "listen");
					System.out.println(response);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		case "request":
			while (true) {
				try {
					response = multicast.ControlChannel(multicast.controlAddress, multicast.controlPort, "Olá", "send");
					System.out.println(response);
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		case "backup":
			try {
				System.out.println("BACKUP\\BACKUP\\BACKUP");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
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