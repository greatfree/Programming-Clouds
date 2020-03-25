package org.greatfree.cache.distributed.store;

import java.util.List;

import org.greatfree.util.Timing;

// Created: 06/11/2018, Bing Li
class TimingReplicateNotification<Value extends Timing>
{
	private String cacheKey;
	private Value value;
	private List<Value> values;
	
	public TimingReplicateNotification(Value value)
	{
		this.value = value;
		this.values = null;
	}
	
	public TimingReplicateNotification(String cacheKey, List<Value> values)
	{
		this.cacheKey = cacheKey;
		this.value = null;
		this.values = values;
	}
	
	public TimingReplicateNotification(String cacheKey, Value value)
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
