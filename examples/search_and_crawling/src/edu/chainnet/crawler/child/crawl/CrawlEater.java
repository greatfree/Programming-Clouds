package edu.chainnet.crawler.child.crawl;

import org.greatfree.concurrency.Consumable;
import org.greatfree.util.NullObject;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.crawler.CrawlConfig;

// Created: 02/20/2014, Bing Li
class CrawlEater implements Consumable<NullObject>
{

	@Override
	public void consume(NullObject arg0)
	{
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				CrawlScheduler.CRAWL().submit();
			}
			catch (InterruptedException e)
			{
				CrawlPrompts.print(e);
			}
			if (CrawlScheduler.CRAWL().isPaused())
			{
				try
				{
					CrawlScheduler.CRAWL().holdOn(CrawlConfig.PAUSE_TIME);
				}
				catch (InterruptedException e)
				{
					CrawlPrompts.print(e);
				}
			}
		}
	}
}

