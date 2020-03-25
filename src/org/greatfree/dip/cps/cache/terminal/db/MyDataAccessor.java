package org.greatfree.dip.cps.cache.terminal.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 07/08/2018, Bing Li
class MyDataAccessor
{
	private PrimaryIndex<String, MyDataEntity> primaryKey;
	
	public MyDataAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, MyDataEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, MyDataEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
