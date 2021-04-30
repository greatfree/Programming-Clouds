package edu.chainnet.sc.collaborator.child.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * BCNode is saved in the Berkeley Object-Oriented DB for Java. 10/18/2020, Bing Li 
 */

// Created: 10/18/2020, Bing Li
class BCNodeAccessor
{
	private PrimaryIndex<String, BCNodeEntity> primaryKey;
	
	public BCNodeAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, BCNodeEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, BCNodeEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}
}
