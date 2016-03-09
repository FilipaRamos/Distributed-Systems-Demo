import java.io.File;

public class ManageDisk {
	// save the partition size that is available to the backup service
	public long freeSpace;
	// save the name of the disk to use
	public String disk;
	// file
	public File file;
	
	public ManageDisk(String disk){
		
		this.disk = disk;
		
		file = new File(this.disk);
		
    	long totalSpace = file.getTotalSpace(); //total disk space in bytes.
    	long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
    	long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.
    	
    	System.out.println(" === Partition Detail ===");
    	System.out.println("Total size : " + totalSpace /1024 /1024 /1024 + " GB");
    	System.out.println("Usable space : " + usableSpace /1024 /1024 /1024 + " GB");
    	System.out.println("Space free : " + freeSpace /1024 /1024 / 1024 + " GB");
    	
    	this.freeSpace = freeSpace;
    	
	}
	
	// to evaluate again the size of the partition
	public void evaluateDisk(){
		long freeSpace = file.getFreeSpace();
		
		this.freeSpace = freeSpace;
	}

}
