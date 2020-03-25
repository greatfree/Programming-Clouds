package org.greatfree.testing.crawlserver;

import org.greatfree.reuse.ThreadDisposable;

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
		try
		{
			r.dispose();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Dispose one instance of HubCrawlConsumer with time to wait being considered. It is not implemented yet. Just leave the interface. 11/20/2014, Bing Li
	 */
	@Override
	public void dispose(CrawlConsumer r, long time)
	{
		try
		{
			r.dispose();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
