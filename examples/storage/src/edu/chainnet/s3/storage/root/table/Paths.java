package edu.chainnet.s3.storage.root.table;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.util.UtilConfig;

import edu.chainnet.s3.storage.StorageConfig;

/*
 * In the current version, multiple children work on the same machine. They have to be assigned with a unique directory on the disk to keep the correction of partitions. The cache is responsible for the synchronization and paths mapping among storage children.
 */

// Created: 09/13/2020, Bing Li
public class Paths
{
	// With respect to the child ID, its partition index and paths on the disk can be found from the map. 09/13/2020, Bing Li
	private CacheMap<ChildPath, ChildPathFactory> paths;
	private AtomicInteger currentCount;

	private final static Logger log = Logger.getLogger("edu.chainnet.s3.storage.root.table");

	private Paths()
	{
	}

	private static Paths instance = new Paths();
	
	public static Paths STORE()
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
				.cacheKey(StorageConfig.CHILD_PATHS)
				.cacheSize(StorageConfig.CHILD_PATH_CACHE_SIZE)
				.offheapSizeInMB(StorageConfig.CHILD_PATH_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(StorageConfig.CHILD_PATH_DISK_SIZE_IN_MB)
				.build();

		this.currentCount = new AtomicInteger(0);
	}
	
	public synchronized ChildPath getPath(String sssPath, String filePath)
	{
		String childID = StorageConfig.CHILD + this.currentCount.incrementAndGet();
		ChildPath p;
		if (this.paths.containsKey(childID))
		{
			p = this.paths.get(childID);
			log.info(p.toString());
		}
		else
		{
			sssPath = sssPath + childID + UtilConfig.FORWARD_SLASH;
			log.info("StorageChild-start(): sssPath = " + sssPath);
			filePath = filePath + childID + UtilConfig.FORWARD_SLASH;
			log.info("StorageChild-start(): filePath = " + filePath);
			p = new ChildPath(childID, sssPath, filePath);
			this.paths.put(childID, p);
		}
		return p;
	}
}
