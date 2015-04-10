package com.greatfree.testing.crawlserver;

import com.greatfree.reuse.ThreadDisposable;

/*
 * The class is responsible for disposing the instance of HubCrawlConsumer in a threader. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class CrawlConsumerDisposer implements ThreadDisposable<CrawlConsumer>
{
	/*
	 * Dispose one instance of HubCrawlConsumer. 11/20/2014, Bing Li
	 */
	@Override
	public void dispose(CrawlConsumer r)
	{
		r.dispose();
	}

	/*
	 * Dispose one instance of HubCrawlConsumer with time to wait being considered. It is not implemented yet. Just leave the interface. 11/20/2014, Bing Li
	 */
	@Override
	public void dispose(CrawlConsumer r, long time)
	{
		r.dispose();
	}
}
