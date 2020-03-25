package org.greatfree.dip.cps.cache.terminal.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 02/25/2019, Bing Li
class MyUKAccessor
{
	private PrimaryIndex<String, MyUKEntity> primaryKey;
	
	public MyUKAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, MyUKEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, MyUKEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
