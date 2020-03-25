package org.greatfree.cache.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/07/2018, Bing Li
@Entity
public class LinearIndexEntity
{
	@PrimaryKey
	private String key;
	
	private Integer headIndex;
	private Integer tailIndex;

	public LinearIndexEntity()
	{
	}
	
	public LinearIndexEntity(String key, Integer headIndex, Integer tailIndex)
	{
		this.key = key;
		this.headIndex = headIndex;
		this.tailIndex = tailIndex;
	}

	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public void setHeadIndex(Integer headIndex)
	{
		this.headIndex = headIndex;
	}
	
	public Integer getHeadIndex()
	{
		return this.headIndex;
	}
	
	public void setTailIndex(Integer tailIndex)
	{
		this.tailIndex = tailIndex;
	}
	
	public Integer getTailIndex()
	{
		return this.tailIndex;
	}
}
