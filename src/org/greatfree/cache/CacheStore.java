package org.greatfree.cache;

import org.greatfree.exceptions.StoreOverflowException;

// Created: 04/08/2017, Bing Li
abstract class CacheStore
{
	private final int storeSize;
	
	public CacheStore(int storeSize)
	{
		this.storeSize = storeSize;
	}
	
	public void isOverflow(String cacheKey, int currentSize) throws StoreOverflowException
	{
		if (this.storeSize < currentSize)
		{
			throw new StoreOverflowException(cacheKey);
		}
	}

	public int getStoreSize()
	{
		return this.storeSize;
	}
}
