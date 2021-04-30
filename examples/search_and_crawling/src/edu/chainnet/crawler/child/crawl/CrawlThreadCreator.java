package edu.chainnet.crawler.child.crawl;

import org.greatfree.concurrency.batch.BalancedThreadCreatable;

import edu.chainnet.crawler.HubSource;

// Created: 04/24/2021, Bing Li
class CrawlThreadCreator implements BalancedThreadCreatable<HubSource, CrawlNotifier, CrawlThread>
{

	@Override
	public CrawlThread createBalanceThreadInstance(int taskSize, CrawlNotifier notifier)
	{
		return new CrawlThread(taskSize, notifier);
	}

}
