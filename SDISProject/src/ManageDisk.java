import java.io.File;

public class ManageDisk {
	// save the partition size that is available to the backup service
	public long freeSpace;
	// save the name of the disk to use
	public String disk;
	
	public ManageDisk(String disk){
		
		this.disk = disk;
		
		File file = new File(this.disk);
		
    	long totalSpace = file.getTotalSpace(); //total disk space in bytes.
    	long usableSpace = file.getUsableSpace(); ///unallocated / free disk space in bytes.
    	long freeSpace = file.getFreeSpace(); //unallocated / free disk space in bytes.
    	
    	System.out.println(" === Disk Detail ===");
    	System.out.println("Total size : " + totalSpace /1024 /1024 /1024 + " GB");
    	System.out.println("Usable space : " + usableSpace /1024 /1024 /1024 + " GB");
    	System.out.println("Space free : " + freeSpace /1024 /1024 / 1024 + " GB");
    	
    	this.freeSpace = usableSpace/3;
    	
    	System.out.println(" === Partiotion Detail === ");
    	System.out.println("Partition Size : " + this.freeSpace /1024 /1024 /1024 + " GB");
	}

}
