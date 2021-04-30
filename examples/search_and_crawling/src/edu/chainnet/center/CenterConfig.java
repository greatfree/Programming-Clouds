package edu.chainnet.center;

import java.util.List;

import org.greatfree.server.container.PeerContainer;

/**
 * @author libing
 *
 * 04/22/2021, Bing Li
 *
 */
public class CenterConfig
{
	public final static String CENTER_HOME = "/home/libing/Wind/Collaboration/ChainNet/DataCenter/";

	public final static String CENTER_COORDINATOR = "DataCenterCoordinator";
	public final static String CENTER_COORDINATOR_KEY = PeerContainer.getPeerKey(CENTER_COORDINATOR);
	
	public final static int CENTER_COORDINATOR_PORT = 8901;
	
	public final static String CONFIG_PATH = CENTER_HOME + "Config/Center/";
	public final static String DOCS_HOME = CenterConfig.CENTER_HOME + "Data/CrawledPages/";
	public final static String INDEX_HOME = CenterConfig.CENTER_HOME + "Data/IndexedPages/";
	public final static String CACHE_HOME = CenterConfig.CENTER_HOME + "Data/Cache/";

	public final static String CHILD = "CenterChild";
	public final static String CHILD_PATHS = "ChildPaths";
	public final static int CHILD_PATH_CACHE_SIZE = 100;
	public final static int CHILD_PATH_OFFHEAP_SIZE_IN_MB = 2;
	public final static int CHILD_PATH_DISK_SIZE_IN_MB = 5;

	public final static String PAGE_MAP = "PageMap";
	public final static int PAGE_MAP_CACHE_SIZE = 10000;
	public final static int PAGE_MAP_OFFHEAP_SIZE_IN_MB = 100;
	public final static int PAGE_MAP_DISK_SIZE_IN_MB = 5000;
	
	public final static String PAGE_QUEUE = "PageQueue";
	public final static int PAGE_QUEUE_CACHE_SIZE = 100;
	public final static int PAGE_QUEUE_OFFHEAP_SIZE_IN_MB = 20;
	public final static int PAGE_QUEUE_DISK_SIZE_IN_MB = 80;
	
	public final static int ONE_TIME_INDEXING_SIZE = 100;
	
	public final static int INDEXING_SCHEDULER_POOL_SIZE = 20;
	public final static long INDEXING_SCHEDULER_KEEP_ALIVE_TIME = 5000;
	public final static long INDEXING_INIT_DELAY = 5000;
	public final static long INDEXING_PERIOD = 5000;
	public final static long INDEXING_SHUTDOWN_TIMEOUT = 2000;
	
	public final static String CONTENT_FIELD = "contents";
	public final static String PATH_FIELD = "path";
//	public final static int HITS_PER_PAGE = 10;
	public final static int HITS_PER_PAGE = 1000;
	public final static List<PageIndex> NO_RESULTS = null;
}
