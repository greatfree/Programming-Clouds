package com.greatfree.testing.coordinator.crawling;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * This is a registry to assist the management of all of the crawlers. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlRegistry
{
	// A list that contains all of the keys of the crawlers. 11/25/2014, Bing Li
	private List<String> crawlDCKeys;
	// It is the current registered total count of URLs. 11/25/2014, Bing Li
	private long totalURLCount;
	// A flag represents why a crawler has any tasks. If it is false, it denotes that it is necessary to distribute crawling load again, i.e., resetting. 11/25/2014, Bing Li
	private boolean shouldReset;

	private CrawlRegistry()
	{
		this.crawlDCKeys = new CopyOnWriteArrayList<String>();
		this.totalURLCount = 0;
		this.shouldReset = false;
	}
	
	/*
	 * A singleton definition. 11/25/2014, Bing Li
	 */
	private static CrawlRegistry instance = new CrawlRegistry();
	
	public static CrawlRegistry COORDINATE()
	{
		if (instance == null)
		{
			instance = new CrawlRegistry();
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
		this.crawlDCKeys.clear();
	}

	/*
	 * Register a new online crawler and its URL load. 11/25/2014, Bing Li
	 */
	public synchronized void register(String dcKey, long localURLCount)
	{
		// Check whether the list contains the key of the crawler. 11/25/2014, Bing Li
		if (!this.crawlDCKeys.contains(dcKey))
		{
			// Put the crawler key into the list. 11/25/2014, Bing Li
			this.crawlDCKeys.add(dcKey);
		}
		// Accumulate the URL load. 11/25/2014, Bing Li
		this.totalURLCount += localURLCount;
		// Detect whether the URL load of the crawler is zero, it represents that the crawler has no workload. Thus, it is required to reset the crawling load to the crawlers. 11/25/2014, Bing Li
		if (localURLCount == 0)
		{
			// Set the reset flag. 11/25/2014, Bing Li
			this.shouldReset = true;
		}
	}

	/*
	 * Unregister a crawler. 11/25/2014, Bing Li
	 */
	public synchronized void unregister(String dcKey)
	{
		// Check whether the list contains the crawler key. 11/25/2014, Bing Li
		if (this.crawlDCKeys.contains(dcKey))
		{
			// Remove the crawler key. 11/25/2014, Bing Li
			this.crawlDCKeys.remove(dcKey);
		}
	}

	/*
	 * Expose the total registered URL count. 11/25/2014, Bing Li
	 */
	public synchronized long getTotalURLCount()
	{
		return this.totalURLCount;
	}

	/*
	 * Expose the total registered crawler count. 11/25/2014, Bing Li
	 */
	public int getCrawlDCCount()
	{
		return this.crawlDCKeys.size();
	}

	/*
	 * Expose the total registered crawler keys. 11/25/2014, Bing Li
	 */
	public List<String> getCrawlDCKeys()
	{
		return this.crawlDCKeys;
	}

	/*
	 * Clear the total registered URL count. 11/25/2014, Bing Li
	 */
	public synchronized void clearURLCount()
	{
		this.totalURLCount = 0;
	}

	/*
	 * Check whether the crawling load should be redistributed. 11/25/2014, Bing Li
	 */
	public synchronized boolean shouldReset()
	{
		return this.shouldReset;
	}
}
