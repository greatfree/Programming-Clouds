package edu.chainnet.crawler.child.crawl;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

// Created: 04/24/2021, Bing Li
class HubSourceAccessor
{
	private PrimaryIndex<String, HubSourceEntity> primaryKey;
	
	public HubSourceAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, HubSourceEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, HubSourceEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
