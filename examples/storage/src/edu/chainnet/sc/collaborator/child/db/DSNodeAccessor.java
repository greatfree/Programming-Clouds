package edu.chainnet.sc.collaborator.child.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * DSNode is saved in the Berkeley Object-Oriented DB for Java. 10/18/2020, Bing Li 
 */

// Created: 10/18/2020, Bing Li
class DSNodeAccessor
{
	private PrimaryIndex<String, DSNodeEntity> primaryKey;
	
	public DSNodeAccessor(EntityStore store)
	{
		this.primaryKey = store.getPrimaryIndex(String.class, DSNodeEntity.class);
	}
	
	public void dispose()
	{
		this.primaryKey = null;
	}
	
	public PrimaryIndex<String, DSNodeEntity> getPrimaryKey()
	{
		return this.primaryKey;
	}

}
