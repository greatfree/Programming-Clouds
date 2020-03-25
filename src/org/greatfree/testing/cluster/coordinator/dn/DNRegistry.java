package org.greatfree.testing.cluster.coordinator.dn;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * This is a registry to assist the management of all of the DNs. 11/25/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class DNRegistry
{
	// A list that contains all of the keys of the DNs. 11/25/2014, Bing Li
	private List<String> dnKeys;
	// The value represents whether the cluster is ready or not. 11/23/2016, Bing Li
	private AtomicBoolean isReady;

	private DNRegistry()
	{
		this.dnKeys = new CopyOnWriteArrayList<String>();
		this.isReady = new AtomicBoolean(false);
	}
	
	/*
	 * A singleton definition. 11/25/2014, Bing Li
	 */
	private static DNRegistry instance = new DNRegistry();
	
	public static DNRegistry COORDINATE()
	{
		if (instance == null)
		{
			instance = new DNRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the registry. 11/25/2014, Bing Li
	 */
	public void dispose()
	{
		this.dnKeys.clear();
	}

	/*
	 * Register a new online DN. 11/25/2014, Bing Li
	 */
	public void register(String dnKey)
	{
		// Check whether the list contains the key of the DN. 11/25/2014, Bing Li
		if (!this.dnKeys.contains(dnKey))
		{
			// Put the DN key into the list. 11/25/2014, Bing Li
			this.dnKeys.add(dnKey);
		}
	}

	/*
	 * Unregister a DN. 11/25/2014, Bing Li
	 */
	public void unregister(String dnKey)
	{
		// Check whether the list contains the DN key. 11/25/2014, Bing Li
		if (this.dnKeys.contains(dnKey))
		{
			// Remove the DN key. 11/25/2014, Bing Li
			this.dnKeys.remove(dnKey);
		}
	}
	
	/*
	 * Expose the total registered DN count. 11/25/2014, Bing Li
	 */
	public int getDNCount()
	{
		return this.dnKeys.size();
	}
	
	/*
	 * Expose the total registered DN keys. 11/25/2014, Bing Li
	 */
	public List<String> getDNKeys()
	{
		return this.dnKeys;
	}

	/*
	 * Check whether the cluster is ready. 11/23/2016, Bing Li
	 */
	public boolean isReady()
	{
		return this.isReady.get();
	}

	/*
	 * Set the cluster ready. 11/23/2016, Bing Li
	 */
	public void setReady()
	{
		this.isReady.set(true);
	}
	
}
