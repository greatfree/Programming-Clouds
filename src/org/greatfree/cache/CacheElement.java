package org.greatfree.cache;

import java.io.Serializable;

// Created: 06/07/2017, Bing Li
public abstract class CacheElement<Key> implements Serializable
{
	private static final long serialVersionUID = 9018336152139357951L;
	
	private Key key;
	
	public CacheElement(Key key)
	{
		this.key = key;
	}
	
	public Key getKey()
	{
		return this.key;
	}
}
