import java.util.*;

public class File {
	// the id of the server to which the original copy of the file belongs to
	public int homeServer;
	// the name of the file
	public String name;
	// the identifier of the file
	public String identifier;
	// Name of the owner of the file
	public String owner;
	// When the file was modified
	public String data;
	// size of the file
	public int size;
	
	// constructor
	public File(int homeServer, String name, int size){
		
		this.homeServer = homeServer;
		this.name = name;
		this.size = size;
		
		extraInfo();
		
	}
	
	public void extraInfo(){
		Scanner input = new Scanner(System.in);
		String choice = input.nextLine();
		
		if(!choice.equals("skip")){
			System.out.println("What's the name of the file owner?");
			this.owner = input.nextLine();
			System.out.println("When was the file last modified?");
		}
		
	}
	
}
