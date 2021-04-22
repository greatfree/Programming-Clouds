package org.greatfree.framework.cps.cache.terminal.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 07/11/2018, Bing Li
@Entity
class MyPointingEntity
{
	@PrimaryKey
	private String key;

	private float points;
	private String description;
	
	public MyPointingEntity()
	{
	}
	
	public MyPointingEntity(String key, float points, String description)
	{
		this.key = key;
		this.points = points;
		this.description = description;
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

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
