package org.greatfree.dip.cps.cache.terminal.db;

import java.util.Date;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 08/08/2018, Bing Li
@Entity
class MyStoreDataEntity
{
	@PrimaryKey
	private String key;

	private String cacheKey;
	private int value;
	private Date time;
	
	public MyStoreDataEntity()
	{
	}
	
	public MyStoreDataEntity(String key, String cacheKey, int value, Date time)
	{
		this.key = key;
		this.cacheKey = cacheKey;
		this.value = value;
		this.time = time;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}

	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public void setValue(int value)
	{
		this.value = value;
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public void setTime(Date time)
	{
		this.time = time;
	}
	
	public Date getTime()
	{
		return this.time;
	}
	
}
