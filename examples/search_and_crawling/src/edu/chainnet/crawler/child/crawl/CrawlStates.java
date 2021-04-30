package edu.chainnet.crawler.child.crawl;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.cache.local.CacheQueue;

import edu.chainnet.crawler.CrawlConfig;

// Created: 04/24/2021, Bing Li
public class CrawlStates
{
	private CacheMap<Boolean, CrawlDoneMapFactory> isCrawlDone;
	
	private CacheQueue<String, CrawlingTaskQueueFactory> hubQueue;
	
	private AtomicBoolean isReady;

	private CrawlStates()
	{
		
	}

	private static CrawlStates instance = new CrawlStates();
	
	public static CrawlStates CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlStates();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException
	{
		if (this.isCrawlDone != null)
		{
			this.isCrawlDone.close();
			this.hubQueue.shutdown();
		}
	}

	public void init()
	{
		if (this.isCrawlDone == null)
		{
			this.isCrawlDone = new CacheMap.CacheMapBuilder<Boolean, CrawlDoneMapFactory>()
					.factory(new CrawlDoneMapFactory())
					.rootPath(ChildProfile.CRAWL().getCachePath())
					.cacheKey(CrawlCacheConfig.IS_CRAWL_DONE_MAP_CACHE_KEY)
					.cacheSize(CrawlCacheConfig.IS_CRAWL_DONE_MAP_CACHE_SIZE)
					.offheapSizeInMB(CrawlCacheConfig.IS_CRAWL_DONE_MAP_CACHE_OFFHEAP_SIZE_IN_MB)
					.diskSizeInMB(CrawlCacheConfig.IS_CRAWL_DONE_MAP_CACHE_DISK_SIZE_IN_MB)
					.build();
		}

		if (this.hubQueue == null)
		{
			this.hubQueue = new CacheQueue.PersistableQueueBuilder<String, CrawlingTaskQueueFactory>()
					.factory(new CrawlingTaskQueueFactory())
					.rootPath(ChildProfile.CRAWL().getCachePath())
					.queueKey(CrawlCacheConfig.HUB_QUEUE_CACHE_KEY)
					.cacheSize(CrawlCacheConfig.HUB_QUEUE_CACHE_SIZE)
					.offheapSizeInMB(CrawlCacheConfig.HUB_QUEUE_CACHE_OFFHEAP_SIZE_IN_MB)
					.diskSizeInMB(CrawlCacheConfig.HUB_QUEUE_CACHE_DISK_SIZE_IN_MB)
					.build();
		}
		
		if (this.isReady == null)
		{
			this.isReady = new AtomicBoolean(false);
		}
	}

	public boolean isReady()
	{
		return this.isReady.get();
	}
	
	public void enable()
	{
		this.isReady.set(true);
	}
	
	public void disable()
	{
		this.isReady.set(false);
	}
	
	public long getTaskSize()
	{
		return this.hubQueue.getMemCacheSize();
	}
	
	public void reset()
	{
		this.hubQueue.clear();
		Set<String> keys = this.isCrawlDone.getKeys();
		for (String key : keys)
		{
			this.isCrawlDone.put(key, false);
		}
	}
	
	public void recharge(Set<String> hubKeys)
	{
		for (String hubKey : hubKeys)
		{
			this.hubQueue.enqueue(hubKey);
			if (!this.isCrawlDone.containsKey(hubKey))
			{
				this.isCrawlDone.put(hubKey, false);
			}
		}
	}
	
	public void done(String hubKey)
	{
		if (this.isCrawlDone.containsKey(hubKey))
		{
			this.isCrawlDone.put(hubKey, true);
		}
	}
	
	public String take()
	{
		if (this.isReady.get())
		{
			long count = this.hubQueue.getMemCacheSize();
			int index = 0;
			do
			{
				String hubKey = this.hubQueue.peek();
				if (hubKey != null)
				{
					if (!this.isCrawlDone.get(hubKey))
					{
						this.isCrawlDone.put(hubKey, false);
						this.hubQueue.dequeue();
						return hubKey;
					}
					else
					{
						if (this.isCrawlDone.get(hubKey))
						{
							this.hubQueue.dequeue();
							return hubKey;
						}
						index++;
					}
				}
				else
				{
					return CrawlConfig.NO_HUB_KEY;
				}
			}
			while (index < count);
		}
		return CrawlConfig.NO_HUB_KEY;
	}
}

