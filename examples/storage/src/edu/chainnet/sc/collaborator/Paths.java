package edu.chainnet.sc.collaborator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.util.UtilConfig;

import edu.chainnet.sc.SCConfig;
import edu.chainnet.sc.collaborator.child.db.DBConfig;

/*
 * Each child of the collaborator needs to set up a DB on the disk. Since I need to test the system on a single computer, it is necessary for those children to exploit different paths. The program keeps such a consistency. 10/20/2020, Bing Li
 */

// Created: 10/20/2020, Bing Li
class Paths
{
	private final static Logger log = Logger.getLogger("edu.chainnet.sc.collaborator");

	private CacheMap<ChildPath, ChildPathFactory> paths;
	private AtomicInteger currentCount;
	private String dbHome;

	private Paths()
	{
	}

	private static Paths instance = new Paths();
	
	public static Paths DB()
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

	public void init(String dbHome)
	{
		this.paths = new CacheMap.CacheMapBuilder<ChildPath, ChildPathFactory>()
				.factory(new ChildPathFactory())
				.rootPath(SCConfig.SC_HOME)
				.cacheKey(SCConfig.CHILD_PATHS)
				.cacheSize(SCConfig.CHILD_PATH_CACHE_SIZE)
				.offheapSizeInMB(SCConfig.CHILD_PATH_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(SCConfig.CHILD_PATH_DISK_SIZE_IN_MB)
				.build();

		this.currentCount = new AtomicInteger(0);
		this.dbHome = dbHome;
	}
	
	public synchronized ChildPath getPath()
	{
		String childID = SCConfig.CHILD + this.currentCount.incrementAndGet();
		ChildPath p;
		if (this.paths.containsKey(childID))
		{
			p = this.paths.get(childID);
			log.info(p.toString());
		}
		else
		{
//			String bcPath = DBConfig.DB_HOME + childID + UtilConfig.FORWARD_SLASH + DBConfig.BCNODE_DB_PATH;
			String bcPath = this.dbHome + childID + UtilConfig.FORWARD_SLASH + DBConfig.BCNODE_DB_PATH;
			log.info("bcPath = " + bcPath);
			String dsPath = this.dbHome + childID + UtilConfig.FORWARD_SLASH + DBConfig.DSNODE_DB_PATH;
			log.info("dsPath = " + dsPath);
			String historyBCPath = this.dbHome + childID + UtilConfig.FORWARD_SLASH + DBConfig.HISTORY_BCNODE_DB_PATH;
			log.info("historyBCPath = " + historyBCPath);
			String historyDSPath = this.dbHome + childID + UtilConfig.FORWARD_SLASH + DBConfig.HISTORY_DSNODE_DB_PATH;
			log.info("historyDSPath = " + historyDSPath);
			p = new ChildPath(childID, bcPath, dsPath, historyBCPath, historyDSPath);
			this.paths.put(childID, p);
		}
		return p;
	}
}
