
public class ThreadManager implements Runnable{
	
	public String name;
	public Server server;
	
	public ThreadManager(Server server, String name){
		
		this.server = server;
		this.name = name;
		
	}

	@Override
	public void run() {
		
		if(server.operation.equals("PUTCHUNK")){
			
			
			
		}
		
		
	}
	
	

}
