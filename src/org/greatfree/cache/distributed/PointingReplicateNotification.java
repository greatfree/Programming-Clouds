package org.greatfree.cache.distributed;

import java.util.List;

import org.greatfree.util.Pointing;

// Created: 06/10/2018, Bing Li
public class PointingReplicateNotification<Value extends Pointing>
{
	private String cacheKey;
	private Value value;
	private List<Value> values;
	
	public PointingReplicateNotification(Value value)
	{
		this.value = value;
		this.values = null;
	}
	
	public PointingReplicateNotification(String cacheKey, List<Value> values)
	{
		this.cacheKey = cacheKey;
		this.value = null;
		this.values = values;
	}
	
	public PointingReplicateNotification(String cacheKey, Value value)
	{
		this.cacheKey = cacheKey;
		this.value = value;
		this.values = null;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public Value getValue()
	{
		return this.value;
	}
	
	public List<Value> getValues()
	{
		return this.values;
	}
}
