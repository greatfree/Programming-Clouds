package edu.chainnet.crawler.client.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 04/22/2021, Bing Li
class HubAccessor
{
	private PrimaryIndex<String, HubEntity> primaryKey;
	
	public HubAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, HubEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, HubEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}

