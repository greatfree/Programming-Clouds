package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 02/21/2019, Bing Li
class ListIndexAccessor
{
	private PrimaryIndex<String, ListIndexEntity> primaryKey;
	
	public ListIndexAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, ListIndexEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, ListIndexEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
