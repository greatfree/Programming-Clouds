package edu.chainnet.s3.storage.root.table;

import java.io.Serializable;

/*
 * In the current version, multiple children work on the same machine. They have to be assigned with a unique directory on the disk to keep the correction of partitions. The cache is responsible for the synchronization and paths mapping among storage children.
 */

// Created: 09/14/2020, Bing Li
public class ChildPath implements Serializable
{
	private static final long serialVersionUID = 2581536154072944198L;
	
	private String childID;
	private String s3Path;
	private String filePath;
//	private int partitionIndex;
	
//	public ChildPartition(String childID, String s3Path, String filePath, int partitionIndex)
	public ChildPath(String childID, String s3Path, String filePath)
	{
		this.childID = childID;
		this.s3Path = s3Path;
		this.filePath = filePath;
//		this.partitionIndex = partitionIndex;
	}
	
	public String getChildID()
	{
		return this.childID;
	}
	
	public String getS3Path()
	{
		return this.s3Path;
	}
	
	public String getFilePath()
	{
		return this.filePath;
	}

	/*
	public int getPartitionIndex()
	{
		return this.partitionIndex;
	}
	*/
	
	public String toString()
	{
//		return this.childID + ": partitionIndex = " +  + this.partitionIndex + "/ " + this.s3Path + "/ " + this.filePath;
		return this.childID + ": " + this.s3Path + "/ " + this.filePath;
	}
}
