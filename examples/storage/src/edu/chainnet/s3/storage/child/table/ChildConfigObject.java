package edu.chainnet.s3.storage.child.table;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. So the program is abandoned. 09/14/2020, Bing Li
 * 
 * Since it is possible that multiple storage children reside on the same machine, the object is used as an identification to notify all of the children about the issue. 08/21/2020, Bing Li
 */

// Created: 08/21/2020, Bing Li
public class ChildConfigObject implements Serializable
{
	private static final long serialVersionUID = 3176690425159693325L;
	
	private int count;
	private int currentCount;
//	private Map<String, Boolean> s3Paths;
//	private Map<String, Boolean> filePaths;
	private Map<String, StoragePaths> paths;
	
	public ChildConfigObject()
	{
		this.count = 1;
		this.currentCount = 1;
//		this.s3Paths = new HashMap<String, Boolean>();
//		this.filePaths = new HashMap<String, Boolean>();
		this.paths = new HashMap<String, StoragePaths>();
	}
	
	public int getCount()
	{
		return this.count;
	}
	
	public void incrementCount()
	{
		this.count++;
	}
	
	public void addPaths(String childID, String s3Path, String filePath)
	{
		this.paths.put(childID, new StoragePaths(childID, s3Path, filePath));
	}
	
	public Map<String, StoragePaths> getPaths()
	{
		return this.paths;
	}
	
	public void setAssigned(String childID)
	{
		this.paths.get(childID).setAssigned();
	}
	
	public void setUnAssigned(String childID)
	{
		this.paths.get(childID).setUnAssigned();
	}

	/*
	public void addS3Path(String s3Path)
	{
		this.s3Paths.put(s3Path, true);
	}

	public void setS3Path(String s3Path)
	{
		this.s3Paths.put(s3Path, false);
	}
	
	public void addFilePath(String filePath)
	{
		this.filePaths.put(filePath, true);
	}

	public void setFilePath(String filePath)
	{
		this.filePaths.put(filePath, false);
	}
	
	public Map<String, Boolean> getS3Paths()
	{
		return this.s3Paths;
	}
	
	public Map<String, Boolean> getFilePaths()
	{
		return this.filePaths;
	}
	*/
	
	public void incrementCurrentCount()
	{
		this.currentCount++;
	}
	
	public void decrementCurrentCount()
	{
		this.currentCount--;
	}
	
	public int getCurrentCount()
	{
		return this.currentCount;
	}
}
