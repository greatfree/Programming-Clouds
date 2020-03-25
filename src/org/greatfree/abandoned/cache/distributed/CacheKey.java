package org.greatfree.abandoned.cache.distributed;

import java.io.Serializable;

// Created: 11/21/2015, Bing Li
//public abstract class SerializedKey implements Serializable
public abstract class CacheKey<Key> implements Serializable
{
	private static final long serialVersionUID = -7681234462977012140L;
	
	private Key dataKey;
	private String cacheKey;
	
//	public CacheKey(Key key, String cacheKey)
	public CacheKey(Key key)
	{
		this.dataKey = key;
//		this.cacheKey = cacheKey;
	}

	public Key getDataKey()
	{
		return this.dataKey;
	}
	
	/*
	public void setKey(Key key)
	{
		this.dataKey = key;
	}
	*/

	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
}
