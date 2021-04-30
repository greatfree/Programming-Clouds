package edu.chainnet.crawler.child.crawl;

import org.greatfree.concurrency.batch.ThreadNotifiable;

import edu.chainnet.crawler.HubSource;

// Created: 04/24/2021, Bing Li
class CrawlNotifier implements ThreadNotifiable<HubSource>
{

	@Override
	public void done(HubSource arg0)
	{
		CrawlScheduler.CRAWL().keepOn();
	}

	@Override
	public void keepOn()
	{
		CrawlScheduler.CRAWL().keepOn();
	}

	@Override
	public void pause() throws InterruptedException
	{
		CrawlScheduler.CRAWL().setPause();
	}

	@Override
	public void restoreFast(String key) throws InterruptedException
	{
		CrawlScheduler.CRAWL().restoreFast(key);
	}

}
