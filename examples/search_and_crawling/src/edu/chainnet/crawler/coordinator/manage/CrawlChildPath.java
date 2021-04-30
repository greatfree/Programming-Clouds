package edu.chainnet.crawler.coordinator.manage;

import java.io.Serializable;

// Created: 04/26/2021, Bing Li
public final class CrawlChildPath implements Serializable
{
	private static final long serialVersionUID = -2237740511185663051L;

	private String childKey;
	private String cachePath;
	private String docPath;
	
	public CrawlChildPath(String childKey, String cachePath, String docPath)
	{
		this.childKey = childKey;
		this.cachePath = cachePath;
		this.docPath = docPath;
	}

	public String getChildKey()
	{
		return this.childKey;
	}
	
	public String getCachePath()
	{
		return this.cachePath;
	}

	public String getDocPath()
	{
		return this.docPath;
	}
	
	public String toString()
	{
		return this.childKey + ": " + this.cachePath + "/" + this.docPath;
	}
}
