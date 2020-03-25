package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/07/2018, Bing Li
public class SortedListAccessor
{
	private PrimaryIndex<String, SortedListEntity> primaryKey;
	
	public SortedListAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, SortedListEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, SortedListEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
