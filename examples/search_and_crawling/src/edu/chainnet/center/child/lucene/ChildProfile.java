package edu.chainnet.center.child.lucene;

import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.util.UtilConfig;

// Created: 04/27/2021, Bing Li
public final class ChildProfile
{
	private String docPath;
	private String indexPath;
	private String cachePath;
	
	private AtomicBoolean isPaused;
	
	private ChildProfile()
	{
		this.docPath = UtilConfig.EMPTY_STRING;
		this.indexPath = UtilConfig.EMPTY_STRING;
		this.cachePath = UtilConfig.EMPTY_STRING;
		this.isPaused = new AtomicBoolean(false);
	}

	private static ChildProfile instance = new ChildProfile();
	
	public static ChildProfile CENTER()
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
	
	public boolean isPaused()
	{
		return this.isPaused.get();
	}
	
	public void pause()
	{
		this.isPaused.set(true);
	}
	
	public void resume()
	{
		this.isPaused.set(false);
	}

	public void setDocPath(String docPath)
	{
		this.docPath = docPath;
	}
	
	public void setIndexPath(String indexPath)
	{
		this.indexPath = indexPath;
	}
	
	public void setCachePath(String cachePath)
	{
		this.cachePath = cachePath;
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
