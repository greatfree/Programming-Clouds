package org.greatfree.testing.crawlserver;

import org.greatfree.reuse.RunnerDisposable;

/*
 * The disposer collects the instance of crawling thread in the interactive dispatcher. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlThreadDisposer implements RunnerDisposable<CrawlThread>
{
	// Dispose the instance of crawling thread. 11/23/2014, Bing Li
	@Override
	public void dispose(CrawlThread r) throws InterruptedException
	{
		r.dispose();
	}

	// Dispose the instance of crawling thread. 11/23/2014, Bing Li
	@Override
	public void dispose(CrawlThread r, long time) throws InterruptedException
	{
		r.dispose();
	}
}
