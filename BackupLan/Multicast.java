import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast {
	// Control Channel Address and Port
	public String controlAddress;
	public int controlPort;

	// Backup Channel Address and Port
	public String backupAddress;
	public int backupPort;

	// Restore Channel Address and Port
	public String restoreAddress;
	public int restorePort;

	// multicast sockets for each channel
	public MulticastSocket controlSocket = null;
	public MulticastSocket backupSocket = null;
	public MulticastSocket restoreSocket = null;

	// InetAddresses
	public InetAddress controlIP;
	public InetAddress backupIP;
	public InetAddress restoreIP;

	// construtor
	public Multicast(String controlAddress, int controlPort, String backupAddress, int backupPort,
			String restoreAddress, int restorePort) {

		this.controlAddress = controlAddress;
		this.controlPort = controlPort;

		System.out.println("Control Channel Address and Port:");
		System.out.println(this.controlAddress + " " + this.controlPort);

		this.backupAddress = backupAddress;
		this.backupPort = backupPort;

		System.out.println("Backup Channel Address and Port:");
		System.out.println(this.backupAddress + " " + this.backupPort);

		this.restoreAddress = restoreAddress;
		this.restorePort = restorePort;

		System.out.println("Restore Channel Address and Port:");
		System.out.println(this.restoreAddress + " " + this.restorePort);
		
		setSockets();

	}
	
	public void setSockets(){
		
		try {
			controlSocket = new MulticastSocket(controlPort);
			controlIP = InetAddress.getByName(controlAddress);

			// set the time to live for the socket
			controlSocket.setTimeToLive(1);
			
			controlSocket.joinGroup(controlIP);
			
			backupSocket = new MulticastSocket(backupPort);
			backupIP = InetAddress.getByName(backupAddress);
			
			backupSocket.setTimeToLive(1);
			
			backupSocket.joinGroup(backupIP);
			
			restoreSocket = new MulticastSocket(restorePort);
			restoreIP = InetAddress.getByName(restoreAddress);
			
			restoreSocket.setTimeToLive(1);
			
			restoreSocket.joinGroup(restoreIP);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	public void closeSockets(){
		
		controlSocket.close();
		backupSocket.close();
		restoreSocket.close();
		
	}
	
}
