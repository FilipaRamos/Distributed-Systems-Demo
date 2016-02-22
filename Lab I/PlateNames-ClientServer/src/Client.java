import java.util.*;

public class Client {
	// arguments needed for client
	String host_name;
	int port_number;
	String oper;
	ArrayList<String> opnd = new ArrayList<String>();
	
	// data 
	String plate_number;
	String owner_name;
	String result;

	// constructor
	public Client(String hn, int pn, String operation, ArrayList<String> arguments){
		host_name = hn;
		port_number = pn;
		oper = operation;
		opnd = arguments;
		processArguments();
	}	
	
	// distinguish between commands
	void processArguments(){
		if(opnd.get(0) == "register"){
			plate_number = opnd.get(1);
			owner_name = opnd.get(2);
			register(plate_number, owner_name);
		}
		else if(opnd.get(0) == "lookup"){
			owner_name = opnd.get(1);
			lookup(owner_name);
		}
		else
			System.out.println("ERROR");
	}
	
	// register 
	void register(String plate, String owner){
		
	}
	
	// lookup
	void lookup(String owner){
		
	}
	
	// process the results obtained from the server
	void processResults(){
		
	}
	
}