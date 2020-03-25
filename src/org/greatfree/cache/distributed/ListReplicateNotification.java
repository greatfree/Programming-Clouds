package org.greatfree.cache.distributed;

import java.util.List;

import org.greatfree.util.UniqueKey;

// Created: 02/21/2019, Bing Li
public class ListReplicateNotification<Value extends UniqueKey>
{
	private String cacheKey;
	private Value value;
	private List<Value> values;
	
	public ListReplicateNotification(Value value)
	{
		this.value = value;
		this.values = null;
	}
	
	public ListReplicateNotification(String cacheKey, List<Value> values)
	{
		this.cacheKey = cacheKey;
		this.value = null;
		this.values = values;
	}
	
	public ListReplicateNotification(String cacheKey, Value value)
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
