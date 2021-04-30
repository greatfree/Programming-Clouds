package edu.chainnet.crawler.child.crawl;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// Created: 04/24/2021, Bing Li
class CrawlMonitor
{
	private AtomicLong sentPageCount;
	private AtomicInteger stuckThreadCount;

	private CrawlMonitor()
	{
		this.sentPageCount = new AtomicLong(0);
		this.stuckThreadCount = new AtomicInteger(0);
	}

	private static CrawlMonitor instance = new CrawlMonitor();
	
	public static CrawlMonitor MILNER()
	{
		if (instance == null)
		{
			instance = new CrawlMonitor();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void addSentPageCount(int pageCount)
	{
		this.sentPageCount.getAndAdd(pageCount);
	}
	
	public long getSentPageCount()
	{
		return this.sentPageCount.get();
	}
	
	public int getStuckCount()
	{
		return this.stuckThreadCount.get();
	}

	public void incrementStuck()
	{
		this.stuckThreadCount.getAndIncrement();
	}
	
	public void decrementStuck()
	{
		this.stuckThreadCount.getAndDecrement();
	}

}
