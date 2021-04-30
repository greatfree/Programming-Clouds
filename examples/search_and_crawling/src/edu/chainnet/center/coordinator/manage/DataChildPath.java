package edu.chainnet.center.coordinator.manage;

import java.io.Serializable;

// Created: 04/27/2021, Bing Li
public final class DataChildPath implements Serializable
{
	private static final long serialVersionUID = -1482864792955042504L;
	
	private String childKey;
	private String docPath;
	private String indexPath;
	private String cachePath;

	public DataChildPath(String childKey, String docPath, String indexPath, String cachePath)
	{
		this.childKey = childKey;
		this.docPath = docPath;
		this.indexPath = indexPath;
		this.cachePath = cachePath;
	}
	
	public String getChildKey()
	{
		return this.childKey;
	}
	
	public String getDocPath()
	{
		return this.docPath;
	}
	
	public String getIndexPath()
	{
		return this.indexPath;
	}
	
	public String getCachePath()
	{
		return this.cachePath;
	}
}

