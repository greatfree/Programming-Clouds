package org.greatfree.framework.cps.cache.terminal.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 07/11/2018, Bing Li
class MyPointingAccessor
{
	private PrimaryIndex<String, MyPointingEntity> primaryKey;
	
	public MyPointingAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, MyPointingEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, MyPointingEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
