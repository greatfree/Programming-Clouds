package org.greatfree.cache.distributed;

import java.io.Serializable;
import java.util.Map;

// Created: 03/15/2018, Bing Li
// public class MapPlaceNotification
public class MapReplicateNotification<Value extends Serializable>
{
	private String cacheKey;
	private String key;
	private Value value;
	private Map<String, Value> values;
	
	public MapReplicateNotification(String key, Value value)
	{
		this.key = key;
		this.value = value;
		this.values = null;
	}
	
	public MapReplicateNotification(String cacheKey, String key, Value value)
	{
		this.cacheKey = cacheKey;
		this.key = key;
		this.value = value;
		this.values = null;
	}

	public MapReplicateNotification(Map<String, Value> values)
	{
		this.key = null;
		this.value = null;
		this.values = values;
	}

	public MapReplicateNotification(String cacheKey, Map<String, Value> values)
	{
		this.cacheKey = cacheKey;
		this.key = null;
		this.value = null;
		this.values = values;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public String getKey()
	{
		return this.key;
	}

	public Value getValue()
	{
		return this.value;
	}

	public Map<String, Value> getValues()
	{
		return this.values;
	}
}
