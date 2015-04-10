package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.Consumable;
import com.greatfree.util.NullObject;
import com.greatfree.util.TerminateSignal;

/*
 * The process runs in an infinite loop to crawl all of the URLs until the process is terminated. 12/01/2014, Bing Li 
 */

// Created: 11/20/2014, Bing Li
public class CrawlEater implements Consumable<NullObject>
{
	@Override
	public void consume(NullObject food)
	{
		// The crawler must work for ever until the crawling server is shutdown. Thus, the crawler needs to check whether the crawler is terminated. 11/23/2014, ing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// Submit one hub URL to crawl. 11/23/2014, Bing Li
				CrawlScheduler.CRAWL().submit();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			// Check whether the crawling procedure is demanded to be paused. 11/23/2014, Bing Li
			if (CrawlScheduler.CRAWL().isPaused())
			{
				try
				{
					// If so, it is required to hold on. 11/23/2014, Bing Li
					CrawlScheduler.CRAWL().holdOn();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
