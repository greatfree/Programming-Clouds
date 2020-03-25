package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/07/2018, Bing Li
class TimingListAccessor
{
	private PrimaryIndex<String, TimingListEntity> primaryKey;
	
	public TimingListAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, TimingListEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, TimingListEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
