package ClientServer;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class Server implements RemoteInterface {

	public Server() {
	}

	public String sayHello() {
		return "Hello, world!";
	}

	public static void main(String args[]) {

		try {
			System.setProperty("java.rmi.server.hostname", "192.168.236.1");
			Server obj = new Server();
			RemoteInterface stub = (RemoteInterface) UnicastRemoteObject
					.exportObject(obj, 0);

			// Bind the remote object's stub in the registry
			Registry registry = LocateRegistry.getRegistry();
			registry.bind("Hello", stub);

			System.err.println("Server ready");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}
}