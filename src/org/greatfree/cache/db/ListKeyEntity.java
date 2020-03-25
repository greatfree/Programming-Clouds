package org.greatfree.cache.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/07/2018, Bing Li
@Entity
class ListKeyEntity
{
	@PrimaryKey
	private Integer index;
	
	private String key;
	
	public ListKeyEntity()
	{
	}
	
	public ListKeyEntity(Integer index, String key)
	{
		this.index = index;
		this.key = key;
	}

	public void setIndex(Integer index)
	{
		this.index = index;
	}
	
	public Integer getIndex()
	{
		return this.index;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}
}
