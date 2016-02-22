import java.util.ArrayList;

public class Server {
	int port_number;
	ArrayList<String> data = new ArrayList<String>();
	
	// constructor
	public Server(int pn){
		port_number = pn;
	}
	
	// register - returns number of plates on the database
	public int register(String plate_number, String owner_name){
		String register = plate_number + " " + owner_name;
		
		if(!data.contains(register)){
			data.add(register);
			return data.size();
		}
			
		System.out.println("ERROR! Plate number already on the database!");
		return -1;
	}
	
	// lookup
	public String lookup(String plate_number){
			return "falta fazer";
	}
	
}
