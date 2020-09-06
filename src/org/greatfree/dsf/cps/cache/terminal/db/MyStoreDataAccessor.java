package org.greatfree.dsf.cps.cache.terminal.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 08/08/2018, Bing Li
class MyStoreDataAccessor
{
	private PrimaryIndex<String, MyStoreDataEntity> primaryKey;
	
	public MyStoreDataAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, MyStoreDataEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, MyStoreDataEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
