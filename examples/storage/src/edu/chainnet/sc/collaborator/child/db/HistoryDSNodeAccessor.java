package edu.chainnet.sc.collaborator.child.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * Deprecated DSNode is saved in the Berkeley Object-Oriented DB for Java. 10/19/2020, Bing Li 
 */

// Created: 10/19/2020, Bing Li
class HistoryDSNodeAccessor
{
	private PrimaryIndex<String, HistoryDSNodeEntity> primaryKey;
	
	public HistoryDSNodeAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, HistoryDSNodeEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, HistoryDSNodeEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}

}
