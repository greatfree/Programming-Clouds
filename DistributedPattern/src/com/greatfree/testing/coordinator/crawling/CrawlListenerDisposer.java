package com.greatfree.testing.coordinator.crawling;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of CrawlListener by invoking its method of shutdown(). 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlListenerDisposer implements RunDisposable<CrawlListener>
{
	/*
	 * Dispose the instance of CrawlListener. 11/25/2014, Bing Li
	 */
	@Override
	public void dispose(CrawlListener r)
	{
		r.shutdown();
	}

	/*
	 * Dispose the instance of CrawlListener. The method does not make sense to CrawlListener. Just leave it here for the requirement of the interface, RunDisposable<CrawlListener>. 11/25/2014, Bing Li
	 */
	@Override
	public void dispose(CrawlListener r, long time)
	{
		r.shutdown();
	}
}
