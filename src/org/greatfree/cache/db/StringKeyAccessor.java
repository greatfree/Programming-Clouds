package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/22/2017, Bing Li
public class StringKeyAccessor
{
	private PrimaryIndex<String, StringKeyEntity> primaryKey;
	
	public StringKeyAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, StringKeyEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, StringKeyEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
