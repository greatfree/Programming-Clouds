package com.greatfree.testing.coordinator.crawling;

import com.greatfree.reuse.ThreadDisposable;

/*
 * This class aims to dispose the instances of CrawlLoadDistributer. It works with the producer/consumer pattern. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlLoadDistributerDisposer implements ThreadDisposable<CrawlLoadDistributer>
{
	/*
	 * Dispose the instance of CrawlLoadDistributer. 11/25/2014, Bing Li
	 */
	@Override
	public void dispose(CrawlLoadDistributer r)
	{
		r.dispose();
	}

	/*
	 * The method does not make sense in the version. Just leave it here. 11/25/2014, Bing Li
	 */
	@Override
	public void dispose(CrawlLoadDistributer r, long time)
	{
		r.dispose();
	}
}
