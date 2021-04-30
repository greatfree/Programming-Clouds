package edu.chainnet.center.coordinator.manage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.util.UtilConfig;

import edu.chainnet.center.CenterConfig;

// Created: 04/27/2021, Bing Li
public final class Paths
{
	private final static Logger log = Logger.getLogger("edu.chainnet.center.coordinator.manage");

	private CacheMap<DataChildPath, DataChildPathFactory> paths;
	private AtomicInteger currentCount;

	private Paths()
	{
	}

	private static Paths instance = new Paths();
	
	public static Paths CENTER()
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
		this.paths = new CacheMap.CacheMapBuilder<DataChildPath, DataChildPathFactory>()
				.factory(new DataChildPathFactory())
				.rootPath(path)
				.cacheKey(CenterConfig.CHILD_PATHS)
				.cacheSize(CenterConfig.CHILD_PATH_CACHE_SIZE)
				.offheapSizeInMB(CenterConfig.CHILD_PATH_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(CenterConfig.CHILD_PATH_DISK_SIZE_IN_MB)
				.build();

		this.currentCount = new AtomicInteger(0);
	}

	public synchronized DataChildPath getPath(String docPath, String indexPath, String cachePath)
	{
		String childID = CenterConfig.CHILD + this.currentCount.incrementAndGet();
		log.info("childID = " + childID);
		DataChildPath p;
		if (this.paths.containsKey(childID))
		{
			p = this.paths.get(childID);
		}
		else
		{
			docPath = docPath + childID + UtilConfig.FORWARD_SLASH;
			indexPath = indexPath + childID + UtilConfig.FORWARD_SLASH;
			cachePath = cachePath + childID + UtilConfig.FORWARD_SLASH;
			log.info("cachePath = " + cachePath);
			p = new DataChildPath(childID, docPath, indexPath, cachePath);
			this.paths.put(childID, p);
		}
		return p;
	}
}
