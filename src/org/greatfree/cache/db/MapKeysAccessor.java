package org.greatfree.cache.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 05/07/2018, Bing Li
public class MapKeysAccessor
{
	private PrimaryIndex<String, MapKeysEntity> primaryKey;
	
	public MapKeysAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, MapKeysEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, MapKeysEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
