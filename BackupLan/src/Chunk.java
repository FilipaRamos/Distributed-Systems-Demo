import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Chunk {

	public String identifier;
	public int index;
	public byte[] data;
	public int replicationDegree;
	public int actualRepDeg;

	public Chunk(String identifier, int index, byte[] data, int replicationDegree, int actualRepDeg) {

		this.identifier = identifier;
		this.index = index;
		this.data = data;
		this.replicationDegree = replicationDegree;
		this.actualRepDeg = actualRepDeg;

	}

	public void incrementActualDeg() {

		actualRepDeg++;

	}

	// to write the chunk in the disk
	public void writeChunk() {
		String outputFile = identifier + "_" + index;

		File dstFile = new File(outputFile);
		FileOutputStream fileOutputStream = null;
		try {

			fileOutputStream = new FileOutputStream(dstFile);
			fileOutputStream.write(data);
			fileOutputStream.flush();
			fileOutputStream.close();

			System.out.println("Output file : " + identifier + "_" + index + " is successfully saved! ");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
