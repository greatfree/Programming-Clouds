package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/07/2018, Bing Li
public class ListKeyAccessor
{
	private PrimaryIndex<Integer, ListKeyEntity> primaryKey;
	
	public ListKeyAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(Integer.class, ListKeyEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<Integer, ListKeyEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
