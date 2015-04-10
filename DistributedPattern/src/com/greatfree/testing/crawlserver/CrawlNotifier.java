package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.Interactable;

/*
 * This is the notifier that calls back the methods provided by the InteractiveDispatcher to notify the dynamic state of crawling. The InteractiveDispatcher then can perform relevant reactions on those notifications. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlNotifier implements Interactable<HubURL>
{
	/*
	 * Demand the scheduler to pause. 11/23/2014, Bing Li
	 */
	@Override
	public void pause()
	{
		try
		{
			CrawlScheduler.CRAWL().holdOn();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Demand the scheduler to keep on crawling. 11/23/2014, Bing Li
	 */
	@Override
	public void keepOn()
	{
		CrawlScheduler.CRAWL().keepOn();
	}

	/*
	 * Restore a slow thread to fast. 11/23/2014, Bing Li
	 */
	@Override
	public void restoreFast(String key)
	{
		try
		{
			CrawlScheduler.CRAWL().restoreFast(key);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * One hub URL is crawled, thus it is time to enqueue it for later scheduling. 11/23/2014, Bing Li
	 */
	@Override
	public void done(HubURL task)
	{
		CrawlScheduler.CRAWL().enqueue(task);
	}
}
