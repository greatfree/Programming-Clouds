package org.greatfree.dsf.cps.cache.terminal.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 08/21/2018, Bing Li
class MyCacheTimingAccessor
{
	private PrimaryIndex<String, MyCacheTimingEntity> primaryKey;
	
	public MyCacheTimingAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, MyCacheTimingEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, MyCacheTimingEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
