package org.greatfree.cache.db;

import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 02/21/2019, Bing Li
@Entity
public class ListIndexEntity
{
	@PrimaryKey
	private String listKey;

	private List<String> keys;

	public ListIndexEntity()
	{
	}
	
	public ListIndexEntity(String listKey, List<String> keys)
	{
		this.listKey = listKey;
		this.keys = keys;
	}

	public void setListKey(String cacheKey)
	{
		this.listKey = cacheKey;
	}
	
	public String getListKey()
	{
		return this.listKey;
	}
	
	public void setKeys(List<String> keys)
	{
		this.keys = keys;
	}
	
	public List<String> getKeys()
	{
		return this.keys;
	}
}
