package org.greatfree.exceptions;

import org.greatfree.util.UtilConfig;

// Created: 03/16/2019, Bing Li
public class IndexOutOfRangeException extends Exception
{
	private static final long serialVersionUID = -1945525570079894292L;
	
	private String cacheKey;
	private int startIndex;
	private int endIndex;
	private int cacheSize;
	
	public IndexOutOfRangeException(String cacheKey, int startIndex, int endIndex, int cacheSize)
	{
		this.cacheKey = cacheKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.cacheSize = cacheSize;
	}
	
	public IndexOutOfRangeException(String cacheKey, int startIndex, int endIndex)
	{
		this.cacheKey = cacheKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.cacheSize = UtilConfig.ZERO_SIZE;
	}
	
	public IndexOutOfRangeException(String cacheKey, int index)
	{
		this.cacheKey = cacheKey;
		this.startIndex = index;
		this.endIndex = index;
		this.cacheSize = UtilConfig.ZERO_SIZE;
	}

	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
	
	public int getCacheSize()
	{
		return this.cacheSize;
	}
}
