import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerFile {
	// the id of the server to which the original copy of the file belongs to
	public int homeServer;
	// the name of the file
	public String name;
	// the identifier of the file
	public String identifier;
	// Name of the owner of the file
	public String owner;
	// When the file was modified
	public String date;
	// size of the file
	public int size;
	// number of chunks
	public int chunksNo;
	// to save the chunks
	public ArrayList<Chunk> chunks = new ArrayList<Chunk>();

	// constructor
	public ServerFile(int homeServer, String name, int size, String owner)
			throws NoSuchAlgorithmException, FileNotFoundException {

		this.homeServer = homeServer;
		this.name = name;
		this.size = size;
		this.owner = owner;
		this.chunksNo = this.size / 64000 + 1;

		File f = new File(name);
		this.date = Long.toString(f.lastModified());

		MessageDigest md = MessageDigest.getInstance("SHA-256");
		String id = name + date + owner;
		md.update(id.getBytes());
		byte[] digest = md.digest();
		this.identifier = String.format("%064x", new java.math.BigInteger(1,
				digest));

		System.out.println("File belongs to server: " + this.homeServer);
		System.out.println("Filename: " + this.name);
		System.out.println("File Identifier: " + this.identifier);
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

		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(f))) {

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
}