import java.io.Serializable;

public class FileEvent implements Serializable {

	public static final long serialVersionUID = 1L;

	// file's characteristics
	public String sourceDirectory;
	public String filename;
	public long fileSize;
	public byte[] fileData;
	public String status;
	
	// constructor
	public FileEvent() {}

	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

}
