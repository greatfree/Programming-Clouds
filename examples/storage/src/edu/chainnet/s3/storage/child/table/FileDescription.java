package edu.chainnet.s3.storage.child.table;

import org.greatfree.util.UniqueKey;

/*
 * All of slices of each file is persisted at the unique path on the child. Files can be synonyms. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
public class FileDescription extends UniqueKey
{
	private static final long serialVersionUID = -2014817937854962165L;

	private String sessionKey;
	private String fileName;
	private String description;
	
	public FileDescription(String fileNameKey, String sessionKey, String fileName, String description)
	{
		super(fileNameKey);
		this.sessionKey = sessionKey;
		this.fileName = fileName;
		this.description = description;
	}
	
	public String getSessionKey()
	{
		return this.sessionKey;
	}

	public String getFileName()
	{
		return this.fileName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public String toString()
	{
		return this.fileName + ": " + this.description;
	}
}
