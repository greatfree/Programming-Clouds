package edu.chainnet.sc.collaborator.child.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * Deprecated BCNode is saved in the Berkeley Object-Oriented DB for Java. 10/18/2020, Bing Li 
 */

// Created: 10/18/2020, Bing Li
class HistoryBCNodeAccessor
{
	private PrimaryIndex<String, HistoryBCNodeEntity> primaryKey;
	
	public HistoryBCNodeAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, HistoryBCNodeEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, HistoryBCNodeEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
