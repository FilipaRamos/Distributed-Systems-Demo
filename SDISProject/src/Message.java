
public class Message {
	
	// message header info
	public String type;
	public int version;
	public String senderId;
	public String fileId;
	public int chunkNo;
	public int replicationDeg;

	// message constructor
	public Message(String type, int version, 
			String senderId, String fileId, 
			int chunkNo, int replicationDeg){
		
		this.type = type;
		this.version = version;
		this.senderId = senderId;
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.replicationDeg = replicationDeg;
		
	}
	
	// message to reply after storing chunk
	public String storedChunk(){
		StringBuilder nrChunk = new StringBuilder();
		nrChunk.append(chunkNo);
		
		return "STORED" + " " + "1.0" + " " + senderId + " " + fileId + " " + nrChunk.toString() + " " + "CRLF" + "CRLF";
	}
	
	/*
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
	*/
}
