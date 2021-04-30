package edu.chainnet.s3.storage.child.table;

import org.greatfree.util.UniqueKey;

// Created: 07/13/2020, Bing Li
class FilePath extends UniqueKey
{
	private static final long serialVersionUID = -2014817937854962165L;
	
	private String sessionKey;
	private String fileName;
	private String path;
	
	public FilePath(String sessionKey, String fileName, String path)
	{
		super(sessionKey);
		this.fileName = fileName;
		this.path = path;
	}

	public String getSesssionKey()
	{
		return this.sessionKey;
	}
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public String getPath()
	{
		return this.path;
	}
}
