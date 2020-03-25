package org.greatfree.testing.cache.local;

import java.io.Serializable;

// Created: 12/24/2019, Bing Li
class MyObj implements Serializable
{
	private static final long serialVersionUID = -1161521174054171363L;
	
	private String key;
	private int value;
	
	public MyObj(String key, int value)
	{
		this.key = key;
		this.value = value;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public int getValue()
	{
		return this.value;
	}
}
