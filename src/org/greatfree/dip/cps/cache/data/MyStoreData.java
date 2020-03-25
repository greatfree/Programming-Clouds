package org.greatfree.dip.cps.cache.data;

import java.util.Date;

import org.greatfree.cache.StoreElement;

// Created: 08/08/2018, Bing Li
public class MyStoreData extends StoreElement
{
	private static final long serialVersionUID = -4102475915996287959L;
	
	private int value;
	private Date time;

	public MyStoreData(String cacheKey, String key, int value, Date time)
	{
		super(cacheKey, key);
		this.value = value;
		this.time = time;
	}

	public int getValue()
	{
		return this.value;
	}
	
	public Date getTime()
	{
		return this.time;
	}
	
	public String toString()
	{
		return value + "@" + time;
	}
}
