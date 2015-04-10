package com.greatfree.testing.db;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * This class is used to save URLEntity by its primary index. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class URLAccessor
{
	// Define the primary index. 11/25/2014, Bing Li
	private PrimaryIndex<String, URLEntity> primaryIndex;

	/*
	 * Initialize. 11/25/2014, Bing Li
	 */
	public URLAccessor(EntityStore store)
	{
		this.primaryIndex = store.getPrimaryIndex(String.class, URLEntity.class);
	}

	/*
	 * Dispose. 11/25/2014, Bing Li
	 */
	public void dispose()
	{
		this.primaryIndex = null;
	}

	/*
	 * Expose the primary index. 11/25/2014, Bing Li
	 */
	public PrimaryIndex<String, URLEntity> getPrimaryIndex()
	{
		return this.primaryIndex;
	}
}
