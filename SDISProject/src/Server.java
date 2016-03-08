import java.io.*;
import java.io.File;

public class Server {
	// id of the server
	public int id;
	// server constructor
	public Server(int id){
		this.id = id;
	}
	
	// main
	public static void main(String[] args) throws UnsupportedEncodingException{
		if(args.length != 7){
			System.out.println("Wrong number of arguments!");
			System.exit(1);
		}
		
		System.out.println();
	
		Server server = new Server(Integer.parseInt(args[0]));
		Multicast multicast = new Multicast(args[1], Integer.parseInt(args[2]), 
				args[3], Integer.parseInt(args[4]), 
				args[5], Integer.parseInt(args[6]));
		
		System.out.println();
		
		ManageDisk manager = new ManageDisk("e:");
		
	}
	
	// the engine of the server which calls the needed procedures
	public int ServerEngine(){
		return 0;
	}
	
}
