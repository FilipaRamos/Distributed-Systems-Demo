import java.io.*;
import java.net.*;

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

	}

	// channel used for control messages
	public int ControlChannel(String address, int port, String message){
		try{
			this.controlSocket = new MulticastSocket(port);
			controlIP = InetAddress.getByName(address);
			
			// set the time to live for the socket
			this.controlSocket.setTimeToLive(1);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return 1;
	}

	// channel used to process info sent to backup
	public int BackupChannel(String address, int port) {
		try{
			this.backupSocket = new MulticastSocket(port);
			backupIP = InetAddress.getByName(address);
			
			// set the time to live for the socket
			this.backupSocket.setTimeToLive(1);
			
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return 1;
	}

	// channel used to process restore operations
	public int RestoreChannel(String address, int port) {
		try{
			this.restoreSocket = new MulticastSocket(port);
			restoreIP = InetAddress.getByName(address);
			
			// set the time to live for the socket
			this.restoreSocket.setTimeToLive(1);
			
		}catch(IOException e){
			e.printStackTrace();
		}
		return 1;
	}

}
