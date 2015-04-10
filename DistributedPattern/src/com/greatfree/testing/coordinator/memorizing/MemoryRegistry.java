package com.greatfree.testing.coordinator.memorizing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Since a bunch of nodes are composed together to form a distributed memory system, a centralized registry is required to manage all of them. This is what the registry should do. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryRegistry
{
	// A list that contains all of the keys of the memory servers. 11/28/2014, Bing Li
	private List<String> memDCKeys;

	private MemoryRegistry()
	{
		this.memDCKeys = new CopyOnWriteArrayList<String>();
	}
	
	/*
	 * A singleton definition. 11/28/2014, Bing Li
	 */
	private static MemoryRegistry instance = new MemoryRegistry();
	
	public static MemoryRegistry COORDINATE()
	{
		if (instance == null)
		{
			instance = new MemoryRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 11/28/2014, Bing Li
	 */
	public void dispose()
	{
		this.memDCKeys.clear();
	}

	/*
	 * Register a new online memory server and its URL load. 11/28/2014, Bing Li
	 */
	public synchronized void register(String dcKey)
	{
		// Check whether the list contains the key of the memory server. 11/28/2014, Bing Li
		if (!this.memDCKeys.contains(dcKey))
		{
			// Put the memory server key into the list. 11/28/2014, Bing Li
			this.memDCKeys.add(dcKey);
		}
	}

	/*
	 * Unregister a memory server. 11/28/2014, Bing Li
	 */
	public synchronized void unregister(String dcKey)
	{
		// Check whether the list contains the memory server key. 11/28/2014, Bing Li
		if (this.memDCKeys.contains(dcKey))
		{
			// Remove the memory server key. 11/28/2014, Bing Li
			this.memDCKeys.remove(dcKey);
		}
	}

	/*
	 * Expose the total registered memory server count. 11/28/2014, Bing Li
	 */
	public int getCrawlDCCount()
	{
		return this.memDCKeys.size();
	}

	/*
	 * Expose the total registered memory server keys. 11/28/2014, Bing Li
	 */
	public List<String> getCrawlDCKeys()
	{
		return this.memDCKeys;
	}
}
