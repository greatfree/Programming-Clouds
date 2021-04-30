package edu.chainnet.crawler.coordinator.manage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.util.UtilConfig;

import edu.chainnet.crawler.CrawlConfig;

// Created: 04/26/2021, Bing Li
public final class Paths
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.coordinator.manage");

	private CacheMap<CrawlChildPath, CrawlChildPathFactory> paths;
	private AtomicInteger currentCount;

	private Paths()
	{
	}

	private static Paths instance = new Paths();
	
	public static Paths CRAWL()
	{
		if (instance == null)
		{
			instance = new Paths();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.paths.close();
	}

	public void init(String path)
	{
		this.paths = new CacheMap.CacheMapBuilder<CrawlChildPath, CrawlChildPathFactory>()
				.factory(new CrawlChildPathFactory())
				.rootPath(path)
				.cacheKey(CrawlConfig.CHILD_PATHS)
				.cacheSize(CrawlConfig.CHILD_PATH_CACHE_SIZE)
				.offheapSizeInMB(CrawlConfig.CHILD_PATH_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(CrawlConfig.CHILD_PATH_DISK_SIZE_IN_MB)
				.build();

		this.currentCount = new AtomicInteger(0);
	}

	public synchronized CrawlChildPath getPath(String cachePath, String docPath)
	{
		String childID = CrawlConfig.CHILD + this.currentCount.incrementAndGet();
		log.info("childID = " + childID);
		CrawlChildPath p;
		if (this.paths.containsKey(childID))
		{
			p = this.paths.get(childID);
		}
		else
		{
			cachePath = cachePath + childID + UtilConfig.FORWARD_SLASH;
			log.info("cachePath = " + cachePath);
			docPath = docPath + childID + UtilConfig.FORWARD_SLASH;
			p = new CrawlChildPath(childID, cachePath, docPath);
			this.paths.put(childID, p);
		}
		return p;
	}
}
