package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.InteractiveThreadCreatable;

/*
 * The creator generates instances of CrawlThread in the interactive dispatcher. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlThreadCreator implements InteractiveThreadCreatable<HubURL, CrawlNotifier, CrawlThread>
{
	// Create an instance of crawling thread. 11/23/2014, Bing Li
	@Override
	public CrawlThread createInteractiveThreadInstance(int taskSize, CrawlNotifier notifier)
	{
		return new CrawlThread(taskSize, notifier);
	}
}
