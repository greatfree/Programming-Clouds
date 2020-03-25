package org.greatfree.cache.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/07/2018, Bing Li
@Entity
class SortedListEntity
{
	@PrimaryKey
	private String key;
	
//	private Double points;
//	private Long points;
	private Float points;
	
	public SortedListEntity()
	{
	}
	
	public SortedListEntity(String key, Float points)
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

	public void setPoints(Float points)
	{
		this.points = points;
	}
	
	public Float getPoints()
	{
		return this.points;
	}
}
