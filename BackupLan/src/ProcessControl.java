import java.util.ArrayList;

public class ProcessControl implements Runnable{

	public Server server;
	public ArrayList<Message> sendQueue = new ArrayList<Message>(); 
	
	public ProcessControl(Server server){
		this.server = server;
	}
	
	public void processControlQueue(){
		
		Thread processControl = new Thread();
		processControl.start();
		
	}
	
	@Override
	public void run() {
		
		// send the messages on the sendQueue
		while(true){
			
			for(int i = 0; i < sendQueue.size(); i++){
				
				
				
			}
			
		}
		
	}
	
	

}
