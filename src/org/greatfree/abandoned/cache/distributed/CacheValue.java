package org.greatfree.abandoned.cache.distributed;

import java.io.Serializable;

// Created: 07/16/2017, Bing Li
public abstract class CacheValue implements CacheKeyable, Serializable
{
	private static final long serialVersionUID = -5028293939786824306L;
	
	private String cacheKey;
	private String dataKey;
	
	public CacheValue(String cacheKey, String dataKey)
	{
		this.cacheKey = cacheKey;
		this.dataKey = dataKey;
	}
	
	@Override
	public String getDataKey()
	{
		return this.dataKey;
	}

	@Override
	public String getCacheKey()
	{
		return this.cacheKey;
	}

	/*
	@Override
	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}

	@Override
	public void setDataKey(String dataKey)
	{
		this.dataKey = dataKey;
	}
	*/
}
