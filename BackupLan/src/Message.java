
public class Message {

	public String type;
	public String version;
	public String senderId;
	public String fileId;
	public int chunkNr;
	public int replicationDegree;
	public byte[] data;
	
	public Message(String type, String version, String senderId, String fileId, int chunkNr, int replicationDegree, byte[] data){
		
		this.type = type;
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNr = chunkNr;
		this.replicationDegree = replicationDegree;
		this.data = data;
	
	}
	
	
	
	
}
