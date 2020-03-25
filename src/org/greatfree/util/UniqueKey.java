package org.greatfree.util;

import java.io.Serializable;

// Created: 02/15/2019, Bing Li
public abstract class UniqueKey implements Serializable
{
	private static final long serialVersionUID = 1243246848991994065L;
	
	private String key;
	
	public UniqueKey()
	{
		this.key = Tools.generateUniqueKey();
	}
	
	public UniqueKey(String key)
	{
		this.key = key;
	}

	public String getKey()
	{
		return this.key;
	}
}
