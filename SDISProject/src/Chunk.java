import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Chunk {
	// the id of the file to which the chunk belongs to
	public String fileId;
	// the name of the file
	public String filename;
	// the number of the chunk
	public int chunkNo;
	// the data of the chunk
	public byte[] chunkData;
	
	// constructor
	public Chunk(String fileId, String filename, int chunkNo, byte[] chunkData){
		
		this.fileId = fileId;
		this.filename = filename;
		this.chunkNo = chunkNo;
		this.chunkData = chunkData;
		
	}
	
	// to write the chunk in the disk
	public void writeChunk(Chunk chunk) {
		String outputFile = "E:\\" + chunk.fileId + "_" + chunk.chunkNo;

		File dstFile = new File(outputFile);
		FileOutputStream fileOutputStream = null;
		try {
			
			fileOutputStream = new FileOutputStream(dstFile);
			fileOutputStream.write(chunk.chunkData);
			fileOutputStream.flush();
			fileOutputStream.close();
			
			System.out.println("Output file : " + chunk.filename + " is successfully saved! ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
