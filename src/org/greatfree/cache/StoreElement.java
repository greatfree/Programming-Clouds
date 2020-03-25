package org.greatfree.cache;

import org.greatfree.util.SerializedKey;

// Created: 08/08/2018, Bing Li
public abstract class StoreElement extends SerializedKey
{
	private static final long serialVersionUID = 7817979742881781458L;

	private String cacheKey;
	
	public StoreElement(String cacheKey, String key)
	{
		super(key);
		this.cacheKey = cacheKey;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
}
