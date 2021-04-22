package org.greatfree.framework.cps.cache.terminal.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 02/25/2019, Bing Li
@Entity
class MyUKEntity
{
	@PrimaryKey
	private String key;

	private int points;

	public MyUKEntity()
	{
	}
	
	public MyUKEntity(String key, int points)
	{
		this.key = key;
		this.points = points;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public void setPoints(int points)
	{
		this.points = points;
	}
	
	public int getPoints()
	{
		return this.points;
	}
}
