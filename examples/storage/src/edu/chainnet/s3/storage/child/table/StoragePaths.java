package edu.chainnet.s3.storage.child.table;

import java.io.Serializable;

/*
 * The program keeps the S3 path and the file path. The information is persisted in case that multiple children are located in the same machine. 09/04/2020, Bing Li
 */

// Created: 09/04/2020, Bing Li
public class StoragePaths implements Serializable
{
	private static final long serialVersionUID = -7865754803215120378L;

	private String childID;
	private String s3Path;
	private String filePath;
	private boolean isAssigned;
	
	public StoragePaths(String childID, String s3Path, String filePath)
	{
		this.childID = childID;
		this.s3Path = s3Path;
		this.filePath = filePath;
		this.isAssigned = true;
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
	
	public boolean isAssigned()
	{
		return this.isAssigned;
	}
	
	public void setAssigned()
	{
		this.isAssigned = true;
	}
	
	public void setUnAssigned()
	{
		this.isAssigned = false;
	}
}
