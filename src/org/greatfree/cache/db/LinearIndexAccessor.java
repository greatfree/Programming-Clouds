package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/07/2018, Bing Li
public class LinearIndexAccessor
{
	private PrimaryIndex<String, LinearIndexEntity> primaryKey;
	
	public LinearIndexAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, LinearIndexEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, LinearIndexEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
