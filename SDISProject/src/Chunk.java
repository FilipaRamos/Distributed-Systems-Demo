
public class Chunk {
	// the id of the file to which the chunk belongs to
	public int fileId;
	// the number of the chunk
	public int chunkNo;
	// size of the chunk
	public int chunkSize;
	
	// constructor
	public Chunk(int fileId, int chunkNo, int chunkSize){
		
		this.fileId = fileId;
		this.chunkNo = chunkNo;
		this.chunkSize = chunkSize;
		
	}

}
