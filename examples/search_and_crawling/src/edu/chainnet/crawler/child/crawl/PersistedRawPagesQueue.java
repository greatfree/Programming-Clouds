package edu.chainnet.crawler.child.crawl;

import java.util.List;
import java.util.logging.Logger;

import org.greatfree.cache.local.CacheQueue;

import edu.chainnet.crawler.AuthoritySolrValue;

// Created: 04/24/2021, Bing Li
public class PersistedRawPagesQueue
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.child.crawl");

	private CacheQueue<AuthoritySolrValue, AuthoritySolrQueueFactory> pondQueue;

	private PersistedRawPagesQueue()
	{
	}
	
	private static PersistedRawPagesQueue instance = new PersistedRawPagesQueue();

	public static PersistedRawPagesQueue WWW()
	{
		if (instance == null)
		{
			instance = new PersistedRawPagesQueue();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.pondQueue.shutdown();
	}
	
	public void init(String cacheHome, String cacheKey, int cacheSize, int offheapSize, int diskSize)
	{
		this.pondQueue = new CacheQueue.PersistableQueueBuilder<AuthoritySolrValue, AuthoritySolrQueueFactory>()
				.factory(new AuthoritySolrQueueFactory())
				.rootPath(cacheHome)
				.queueKey(cacheKey)
				.cacheSize(cacheSize)
				.offheapSizeInMB(offheapSize)
				.diskSizeInMB(diskSize)
				.build();
	}
	
	public boolean isEmpty()
	{
		return this.pondQueue.isEmpty();
	}
	
	public long getAuthoritySize()
	{
		return this.pondQueue.getMemCacheSize();
	}
	
	public boolean isDown()
	{
		return this.pondQueue.isClosed();
	}

	public List<AuthoritySolrValue> loadAuthorities(int count)
	{
		return this.pondQueue.dequeue(count);
	}

	public void saveAuthorities(List<AuthoritySolrValue> authorities)
	{
		this.pondQueue.enqueue(authorities);
		int i = 0;
		for (AuthoritySolrValue entry : authorities)
		{
			log.info("=====================================");
			log.info(++i + ") " + entry.title);
//			log.info(entry.content);
			log.info("=====================================");
		}
//		System.out.println("PersistedAuthorityQueue-saveAuthorities(): POND QUEUE SIZE = " + this.pondQueue.getMemCacheSize());
	}
}
