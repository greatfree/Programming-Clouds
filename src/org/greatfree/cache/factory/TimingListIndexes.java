package org.greatfree.cache.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Created: 06/23/2017, Bing Li
public class TimingListIndexes implements Serializable
{
	private static final long serialVersionUID = -6015577932793754133L;
	
//	private Map<Integer, String> keys;
	private List<String> keys;
	private Map<String, Date> timings;
	
	public TimingListIndexes()
	{
//		this.keys = new HashMap<Integer, String>();
		this.keys = new ArrayList<String>();
		this.timings = new HashMap<String, Date>();
	}
	
	public TimingListIndexes(List<String> keys, Map<String, Date> timings)
	{
		this.keys = keys;
		this.timings = timings;
	}
	
	public List<String> getKeys()
	{
		return this.keys;
	}
	
	public String getKey(int index)
	{
		return this.keys.get(index);
	}
	
//	public void addKey(int index, String key)
	public void addKey(String key)
	{
//		this.keys.put(index, key);
		this.keys.add(key);
	}
	
	public int getKeySize()
	{
		return this.keys.size();
	}
	
	public void appendKey(String key)
	{
//		this.keys.put(this.keys.size(), key);
		this.keys.add(key);
	}

	public Date getTime(String key)
	{
		return this.timings.get(key);
	}
	
	public void setTime(String key, Date time)
	{
		this.timings.put(key, time);
	}
	
	public Map<String, Date> getTimings()
	{
		return this.timings;
	}
	
	public void setTimings(Map<String, Date> timings)
	{
		this.timings = timings;
	}
	
	public Date getOldestTime()
	{
		return this.timings.get(this.keys.get(this.keys.size() - 1));
	}

	public int getIndex(String key)
	{
		return this.keys.indexOf(key);
	}

	public void remove(String key)
	{
		this.timings.remove(key);
	}
	
	public void remove(int index)
	{
		this.keys.remove(index);
	}
}
