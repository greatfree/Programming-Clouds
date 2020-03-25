package org.greatfree.dip.cps.cache.terminal.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 07/24/2018, Bing Li
@Entity
class MyCachePointingEntity
{
	@PrimaryKey
	private String key;

	private String cacheKey;
	private float points;
	
	public MyCachePointingEntity()
	{
	}
	
	public MyCachePointingEntity(String cacheKey, String key, float points)
	{
		this.cacheKey = cacheKey;
		this.key = key;
		this.points = points;
	}
	
	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public void setPoints(float points)
	{
		this.points = points;
	}
	
	public float getPoints()
	{
		return this.points;
	}
}
