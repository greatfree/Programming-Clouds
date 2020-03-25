package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/08/2018, Bing Li
public class TimingListIndexAccessor
{
	private PrimaryIndex<String, TimingListIndexEntity> primaryKey;
	
	public TimingListIndexAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, TimingListIndexEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, TimingListIndexEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
