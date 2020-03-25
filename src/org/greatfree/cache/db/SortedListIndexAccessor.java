package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/06/2018, Bing Li
class SortedListIndexAccessor
{
	private PrimaryIndex<String, SortedListIndexEntity> primaryKey;
	
	public SortedListIndexAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, SortedListIndexEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, SortedListIndexEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
