package edu.chainnet.crawler.child.crawl;

import org.greatfree.util.UtilConfig;

// Created: 04/26/2021, Bing Li
public final class ChildProfile
{
	private String cachePath;
	private String docPath;
	
	private ChildProfile()
	{
		this.cachePath = UtilConfig.EMPTY_STRING;
		this.docPath = UtilConfig.EMPTY_STRING;
	}

	private static ChildProfile instance = new ChildProfile();
	
	public static ChildProfile CRAWL()
	{
		if (instance == null)
		{
			instance = new ChildProfile();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void setCachePath(String cachePath)
	{
		this.cachePath = cachePath;
	}

	public void setDocPath(String docPath)
	{
		this.docPath = docPath;
	}

	public String getCachePath()
	{
		return this.cachePath;
	}

	public String getDocPath()
	{
		return this.docPath;
	}
}
