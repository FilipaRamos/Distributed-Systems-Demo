import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerFile {
	// the id of the server to which the original copy of the file belongs to
	public int homeServer;
	// the name of the file
	public String name;
	// the identifier of the file
	public byte[] identifier;
	// Name of the owner of the file
	public String owner;
	// When the file was modified
	public String data;
	// size of the file
	public int size;
	// number of chunks
	public int chunksNo;
	// to save the chunks
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();

	// constructor
	public ServerFile(int homeServer, String name, int size, String owner) {

		this.homeServer = homeServer;
		this.name = name;
		this.size = size;
		this.owner = owner;
		this.chunksNo = this.size / 64000 + 1;

		// create the identifier
		try {
			String fileId = Integer.toString(this.homeServer) + this.name + this.owner;
			this.identifier = hash(fileId);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		System.out.println("File belongs to server: " + this.homeServer);
		System.out.println("Filename: " + this.name);
		System.out.println("File size: " + this.size);
		System.out.println("Owner of the file: " + this.owner);
		System.out.println("Number of chunks to be used: " + this.chunksNo);

	}

	// splits the file in chunks
	public void splitFile(File f) throws FileNotFoundException, IOException {

		int partCounter = 0;

		// maximum size of chunks is 64 kb
		int chunksSize = 64 * 1024;

		byte[] buffer = new byte[chunksSize];

		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f))) {

			String name = f.getName();

			int tmp = 0;
			while ((tmp = bis.read(buffer)) > 0) {
				// write each chunk of data into separate file with different
				Chunk chunk = new Chunk(identifier, name, partCounter, buffer);
				chunks.add(chunk);
				System.out.println("Chunk created -> nr " + partCounter);
			}
		}
	}

	// hash the string (to create file identifier)
	public byte[] hash(String text) throws NoSuchAlgorithmException {

		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

		return hash;
	}

}
