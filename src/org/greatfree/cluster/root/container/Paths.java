package org.greatfree.cluster.root.container;

import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.util.UtilConfig;

// Created: 09/23/2021, Bing Li
public final class Paths
{
	private final static String CHILD_PATHS = "ChildPaths";
	private final static int CHILD_PATH_CACHE_SIZE = 100;
	private final static int CHILD_PATH_OFFHEAP_SIZE_IN_MB = 2;
	private final static int CHILD_PATH_DISK_SIZE_IN_MB = 5;

	private final static String CHILD = "Child";

	private CacheMap<ChildPath, ChildPathFactory> paths;
	private AtomicInteger currentCount;
	
	private Paths()
	{
	}

	private static Paths instance = new Paths();
	
	public static Paths CONFIG()
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
		this.paths = new CacheMap.CacheMapBuilder<ChildPath, ChildPathFactory>()
				.factory(new ChildPathFactory())
				.rootPath(path)
				.cacheKey(Paths.CHILD_PATHS)
				.cacheSize(Paths.CHILD_PATH_CACHE_SIZE)
				.offheapSizeInMB(Paths.CHILD_PATH_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(Paths.CHILD_PATH_DISK_SIZE_IN_MB)
				.build();

		this.currentCount = new AtomicInteger(0);
	}

	public synchronized String getPath(String relativePath)
	{
		String childID = Paths.CHILD + this.currentCount.incrementAndGet();
		ChildPath p;
		if (this.paths.containsKey(childID))
		{
			p = this.paths.get(childID);
		}
		else
		{
			relativePath = relativePath + childID + UtilConfig.FORWARD_SLASH;
			p = new ChildPath(childID, relativePath);
			this.paths.put(childID, p);
		}
		return p.getPath();
	}
}
