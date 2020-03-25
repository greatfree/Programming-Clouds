package org.greatfree.util;

import java.io.Serializable;

// Created: 11/21/2015, Bing Li
public abstract class SerializedKey implements Serializable
{
	private static final long serialVersionUID = -7681234462977012140L;
	
	private String key;
	
	public SerializedKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
}
