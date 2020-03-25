package org.greatfree.dip.cps.cache.terminal.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 07/24/2018, Bing Li
class MyCachePointingAccessor
{
	private PrimaryIndex<String, MyCachePointingEntity> primaryKey;
	
	public MyCachePointingAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, MyCachePointingEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, MyCachePointingEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
