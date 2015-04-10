package com.greatfree.testing.coordinator.memorizing;

import java.util.Set;

import com.greatfree.testing.data.CrawledLink;
import com.greatfree.util.Tools;

/*
 * The coordinator is responsible to send the crawled links to one of the distributed memory servers in a hash-based load-balancing approach. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryCoordinator
{
	private MemoryCoordinator()
	{
	}

	/*
	 * A singleton implementation. 11/25/2014, Bing Li
	 */
	private static MemoryCoordinator instance = new MemoryCoordinator();
	
	public static MemoryCoordinator COORDINATOR()
	{
		if (instance == null)
		{
			instance = new MemoryCoordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the coordinator. 11/25/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
	}

	/*
	 * Send the crawled links to the distributed memory servers. 11/28/2014, Bing Li
	 */
	public void distributeCrawledLink(Set<CrawledLink> links)
	{
		for (CrawledLink link : links)
		{
			// To achieve the goal of load-balance, a hash-based algorithm is used. 11/28/2014, Bing Li
			MemoryEventer.NOTIFY().addCrawledLink(Tools.getClosestKey(link.getKey(), MemoryRegistry.COORDINATE().getCrawlDCKeys()), link);
		}
	}
}
