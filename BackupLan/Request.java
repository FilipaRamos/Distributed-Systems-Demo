
public class Request {
	
	public String type;
	public String path;
	public int replicationDegree;
	public int spaceToReclaim;
	public boolean enhanced;

	public Request(String type, String path, int replicationDegree, int spaceToReclaim, boolean enhanced){
		
		this.type = type;
		this.path = path;
		this.replicationDegree = replicationDegree;
		this.spaceToReclaim = spaceToReclaim;
		this.enhanced = enhanced;
		
	}
	
}
