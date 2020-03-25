package org.greatfree.cache.db;

import java.util.List;
import java.util.Map;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/06/2018, Bing Li
@Entity
class SortedListIndexEntity
{
	@PrimaryKey
	private String listKey;
	
//	private Map<Integer, String> keys;
	private List<String> keys;
	private Map<String, Float> points;
	private List<String> obsoleteKeys;

	public SortedListIndexEntity()
	{
	}
	
//	public PointingListIndexEntity(String cacheKey, Map<Integer, String> keys, Map<String, Double> points)
	public SortedListIndexEntity(String listKey, List<String> keys, Map<String, Float> points, List<String> obsoleteKeys)
	{
		this.listKey = listKey;
		this.keys = keys;
		this.points = points;
		this.obsoleteKeys = obsoleteKeys;
	}

	public void setListKey(String cacheKey)
	{
		this.listKey = cacheKey;
	}
	
	public String getListKey()
	{
		return this.listKey;
	}
	
//	public void setKeys(Map<Integer, String> keys)
	public void setKeys(List<String> keys)
	{
		this.keys = keys;
	}
	
//	public Map<Integer, String> getKeys()
	public List<String> getKeys()
	{
		return this.keys;
	}
	
	public void setPoints(Map<String, Float> points)
	{
		this.points = points;
	}
	
	public Map<String, Float> getPoints()
	{
		return this.points;
	}
	
	public void setObsoleteKeys(List<String> obsKeys)
	{
		this.obsoleteKeys = obsKeys;
	}
	
	public List<String> getObsoleteKeys()
	{
		return this.obsoleteKeys;
	}
}
