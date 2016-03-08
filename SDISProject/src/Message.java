
public class Message {
	
	// message header info
	public String type;
	public int version;
	public int senderId;
	public int fileId;
	public int chunkNo;
	public int replicationDeg;

	// message constructor
	public Message(String type, int version, 
			int senderId, int fileId, 
			int chunkNo, int replicationDeg){
		
		this.type = type;
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
		
	}
	
	// turns strings into ascii code
	public long toAscii(String s){
        StringBuilder sb = new StringBuilder();
        String ascString = null;
        long asciiInt;
                for (int i = 0; i < s.length(); i++){
                    sb.append((int)s.charAt(i));
                    char c = s.charAt(i);
                }
                ascString = sb.toString();
                asciiInt = Long.parseLong(ascString);
                return asciiInt;
    }
	
}
