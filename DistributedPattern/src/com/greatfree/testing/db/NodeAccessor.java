package com.greatfree.testing.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * This class is used to save NodeEntity by its primary index. 10/03/2014, Bing Li
 */

// Created: 10/03/2014, Bing Li
public class NodeAccessor
{
	// Define the primary index. 10/08/2014, Bing Li
	private PrimaryIndex<String, NodeEntity> primaryIndex;

	/*
	 * Initialize. 10/08/2014, Bing Li
	 */
	public NodeAccessor(EntityStore store)
	{
		this.primaryIndex = store.getPrimaryIndex(String.class, NodeEntity.class);
	}

	/*
	 * Dispose. 10/08/2014, Bing Li
	 */
	public void dispose()
	{
		this.primaryIndex = null;
	}

	/*
	 * Expose the primary index. 10/08/2014, Bing Li
	 */
	public PrimaryIndex<String, NodeEntity> getPrimaryIndex()
	{
		return this.primaryIndex;
	}
}
