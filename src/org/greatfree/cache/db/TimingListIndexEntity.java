package org.greatfree.cache.db;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

// Created: 05/08/2018, Bing Li
@Entity
class TimingListIndexEntity
{
	@PrimaryKey
	private String cacheKey;

	private List<String> keys;
	private Map<String, Date> timings;
	
	public TimingListIndexEntity()
	{
	}
	
	public TimingListIndexEntity(String cacheKey, List<String> keys, Map<String, Date> timings)
	{
		this.cacheKey = cacheKey;
		this.keys = keys;
		this.timings = timings;
	}
	

	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
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
	
	public void setTimes(Map<String, Date> timings)
	{
		this.timings = timings;
	}
	
	public Map<String, Date> getPoints()
	{
		return this.timings;
	}

}
