package org.greatfree.cache.distributed.store;

import java.util.List;

import org.greatfree.cache.StoreElement;

/*
 * The version is created and tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 08/08/2018, Bing Li
public class ReplicateNotification<Value extends StoreElement>
{
	private String cacheKey;
	private Value value;
	private List<Value> values;
	
	public ReplicateNotification(Value value)
	{
//		System.out.println("ReplicateNotification() [SINGLE] is initialized!");
		this.cacheKey = value.getCacheKey();
		this.value = value;
		this.values = null;
	}

	public ReplicateNotification(String cacheKey, Value value)
	{
		this.cacheKey = value.getCacheKey();
		this.value = value;
		this.values = null;
	}

	public ReplicateNotification(String cacheKey, List<Value> values)
	{
//		System.out.println("ReplicateNotification() [MULTIPLE] is initialized!");
		this.cacheKey = cacheKey;
		this.value = null;
		this.values = values;
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
