package org.greatfree.cache.factory;

import java.io.Serializable;
import java.util.Set;

// Created: 06/07/2017, Bing Li
//public class MapKeys extends SerializedKey<String>
class MapKeys implements Serializable
{
	private static final long serialVersionUID = 1532651341616576777L;

	private Set<String> keys;
	
//	public MapKeys(String key, Set<String> keys)
	public MapKeys(Set<String> keys)
	{
//		super(key);
		this.keys = keys;
	}

	public Set<String> getKeys()
	{
		return this.keys;
	}
}
