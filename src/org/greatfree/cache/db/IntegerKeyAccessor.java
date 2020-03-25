package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/22/2017, Bing Li
public class IntegerKeyAccessor
{
	private PrimaryIndex<Integer, IntegerKeyEntity> primaryKey;
	
	public IntegerKeyAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(Integer.class, IntegerKeyEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<Integer, IntegerKeyEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
