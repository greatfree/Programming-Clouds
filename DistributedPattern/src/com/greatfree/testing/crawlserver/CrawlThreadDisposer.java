package com.greatfree.testing.crawlserver;

import com.greatfree.reuse.ThreadDisposable;

/*
 * The disposer collects the instance of crawling thread in the interactive dispatcher. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlThreadDisposer implements ThreadDisposable<CrawlThread>
{
	// Dispose the instance of crawling thread. 11/23/2014, Bing Li
	@Override
	public void dispose(CrawlThread r)
	{
		r.dispose();
	}

	// Dispose the instance of crawling thread. 11/23/2014, Bing Li
	@Override
	public void dispose(CrawlThread r, long time)
	{
		r.dispose();
	}
}
