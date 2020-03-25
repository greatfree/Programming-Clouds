package org.greatfree.cache;

import java.util.Date;

import org.greatfree.util.Timing;

//Created: 04/25/2018, Bing Li
public abstract class CacheTiming extends Timing
{
	private static final long serialVersionUID = 8431998466629044906L;
	
	private String cacheKey;
	
	// It is WRONG! How to handle existing index?
//	private int index;

//	public MapTiming(String mapKey, String key, int index, Date time)
	public CacheTiming(String cacheKey, String key, Date time)
	{
		super(key, time);
		this.cacheKey = cacheKey;
//		this.index = index;
	}

	public String getCacheKey()
	{
		return this.cacheKey;
	}

	/*
	public int getIndex()
	{
		return this.index;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
	}
	*/
}
