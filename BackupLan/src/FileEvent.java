import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileEvent {
	// to define the chunk's size
	public int CHUNK_SIZE = 64000;
	// the id of the server to which the original copy of the file belongs to
	public String homeServer;
	// the name of the file
	public String name;
	// the identifier of the file
	public String identifier;
	// When the file was modified
	public String date;
	// size of the file
	public int size;
	// number of chunks
	public int chunksNo;
	// to save the requested replication Degree
	public int replicationDegree;
	// to save the chunks
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();

	// constructor
	public FileEvent(String homeServer, String name, int size, String date, int replicationDegree) {

		this.homeServer = homeServer;
		this.name = name;
		this.size = size;
		this.date = date;
		this.chunksNo = (this.size / (64000)) + 1;
		this.replicationDegree = replicationDegree;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String id = name + date;
			md.update(id.getBytes());
			byte[] digest = md.digest();
			this.identifier = String.format("%064x", new java.math.BigInteger(1, digest));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		System.out.println("Number of chunks to be used: " + this.chunksNo);

	}

	// splits the file in chunks
	public void splitFile(File inputFile, Server server) {

		FileInputStream inputStream;

		System.out.println("...splitting file...");

		int fileSize = (int) inputFile.length();
		int nChunks = 0, read = 0, readLength = CHUNK_SIZE;
		byte[] byteChunkPart;

		try {
			inputStream = new FileInputStream(inputFile);
			while (fileSize > 0) {
				if (fileSize <= 64000) {
					readLength = fileSize;
				}

				byteChunkPart = new byte[readLength];
				read = inputStream.read(byteChunkPart, 0, readLength);
				fileSize -= read;

				assert (read == byteChunkPart.length);
				nChunks++;

				Chunk chunk = new Chunk(identifier, nChunks - 1, byteChunkPart, replicationDegree, 0);
				chunks.add(chunk);

				Message message = new Message("PUTCHUNK", "1.0", server.id, identifier, nChunks - 1, replicationDegree,
						byteChunkPart);
				server.requests.add(message);

				byteChunkPart = null;
			}

			inputStream.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("splitted file");

	}

	// merge the file back together
	public void mergeFile() {

		File file = new File(name);
		FileOutputStream fos;

		try {
			fos = new FileOutputStream(file, true);
			
			for(int i = 0; i < chunks.size(); i++){
				fos.write(chunks.get(i).data);
				fos.close();
			}
			
			fos.flush();
			fos.close();
			fos = null;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// hash the string (to create file identifier)
	public byte[] hash(String text) throws NoSuchAlgorithmException {

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

		return hash;
	}

}
