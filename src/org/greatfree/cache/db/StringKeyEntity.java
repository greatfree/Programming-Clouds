package org.greatfree.cache.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/22/2017, Bing Li
@Entity
public class StringKeyEntity
{
	@PrimaryKey
	private String key;
	
//	@SecondaryKey(relate=MANY_TO_ONE)
//	private String cacheKey;
	
	public StringKeyEntity()
	{
	}
	
//	public StringKeyEntity(String key, String cacheKey)
	public StringKeyEntity(String key)
	{
		this.key = key;
//		this.cacheKey = cacheKey;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}

	/*
	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	*/
}
