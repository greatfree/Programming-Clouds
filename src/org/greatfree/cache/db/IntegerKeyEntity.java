package org.greatfree.cache.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/22/2017, Bing Li
@Entity
public class IntegerKeyEntity
{
	@PrimaryKey
	private Integer key;

//	@SecondaryKey(relate=MANY_TO_ONE)
//	private String cacheKey;

	public IntegerKeyEntity()
	{
	}
	
//	public IntegerKeyEntity(Integer key, String cacheKey)
	public IntegerKeyEntity(Integer key)
	{
		this.key = key;
//		this.cacheKey = cacheKey;
	}
	
	public void setKey(Integer key)
	{
		this.key = key;
	}
	
	public Integer getKey()
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
