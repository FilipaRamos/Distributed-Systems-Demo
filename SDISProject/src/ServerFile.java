import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerFile {
	// to define the chunk's size
	public int CHUNK_SIZE = 64000;
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
	public ServerFile(int homeServer, String name, int size, String owner, String date) {

		this.homeServer = homeServer;
		this.name = name;
		this.size = size;
		this.owner = owner;
		this.date = date;
		this.chunksNo = (this.size / (64000)) + 1;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String id = name + date + owner;
			md.update(id.getBytes());
			byte[] digest = md.digest();
			this.identifier = String.format("%064x", new java.math.BigInteger(1, digest));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		System.out.println("File belongs to server: " + this.homeServer);
		System.out.println("Filename: " + this.name);
		System.out.println("Identifier: " + this.identifier);
		System.out.println("File size: " + this.size);
		System.out.println("Owner of the file: " + this.owner);
		System.out.println("Number of chunks to be used: " + this.chunksNo);

	}

	// splits the file in chunks
	public void splitFile(File inputFile, Server server, int replicationDegree) {

		FileInputStream inputStream;
		String newFileName;
		FileOutputStream filePart;
		StringBuilder chunkNo = new StringBuilder();
		StringBuilder replication = new StringBuilder();
		String header;

		System.out.println("...splitting file...");
		
		int fileSize = (int) inputFile.length();
		int nChunks = 0, read = 0, readLength = CHUNK_SIZE;
		byte[] byteChunkPart;
		
		try{
			inputStream = new FileInputStream(inputFile);
			while (fileSize > 0) {
				if (fileSize <= 64000) {
					readLength = fileSize;
				}
				
				chunkNo.append(nChunks);
				replication.append(server.replicationDegree);
				
				header = server.messageType + " " + 
				"1.0" + " " + 
						server.id + " " + 
				server.serverFile.identifier + " " + 
						chunkNo.toString() + " " + 
				replication.toString() + " " + 
						"CRLF"+"CRLF";
				
				byte[] head = new byte[25];
				head = header.getBytes();
				
				byteChunkPart = new byte[readLength];
				read = inputStream.read(byteChunkPart, 0, readLength);
				fileSize -= read;
				
				assert (read == byteChunkPart.length);
				nChunks++;
				newFileName = identifier + "_" + Integer.toString(nChunks - 1);
				
				filePart = new FileOutputStream(new File(newFileName));
				filePart.write(byteChunkPart);

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
				outputStream.write( head );
				outputStream.write( byteChunkPart );
				System.out.println("added header " + (nChunks-1));

				byte[] completeChunk = outputStream.toByteArray( );
				
				Chunk chunk = new Chunk(identifier, nChunks-1, completeChunk, replicationDegree, 0);
				chunks.add(chunk);
				
				filePart.flush();
				filePart.close();
				byteChunkPart = null;
				filePart = null;
			}
			inputStream.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		System.out.println("splitted file");

	}

	// merge the file back together
	public void mergeFile(String filename) {

		File file = new File("E:\\", filename);
		FileOutputStream fos;
		FileInputStream fis;
		byte[] fileBytes;
		int bytesRead = 0;
		ArrayList<File> list = new ArrayList<File>();

		for (int i = 0; i < list.size(); i++) {
			list.add(new File(file.getName() + ".part" + i));
		}

		try {
			fos = new FileOutputStream(file, true);
			for (File f : list) {

				fis = new FileInputStream(f);
				fileBytes = new byte[(int) f.length()];
				bytesRead = fis.read(fileBytes, 0, (int) f.length());
				assert (bytesRead == fileBytes.length);
				assert (bytesRead == (int) f.length());
				fos.write(fileBytes);
				fos.flush();
				fileBytes = null;
				fis.close();
				fis = null;

			}

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
