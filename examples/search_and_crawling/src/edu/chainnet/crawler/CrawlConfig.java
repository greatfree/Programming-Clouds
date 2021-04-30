package edu.chainnet.crawler;

import java.util.Map;
import java.util.Set;

import org.greatfree.server.container.PeerContainer;
import org.greatfree.util.UtilConfig;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.crawler.child.crawl.CrawlThread;

// Created: 04/22/2021, Bing Li
public class CrawlConfig
{
	public final static String REGISTRY_IP = "127.0.0.1";
	
	public final static int CRAWL_COODINATOR_PORT = 8900;

	public final static String CRAWLING_COORDINATOR = "CrawlingCoordinator";
	public final static String CRAWLING_COORDINATOR_KEY = PeerContainer.getPeerKey(CRAWLING_COORDINATOR);
	
	public final static String NO_HUB_KEY = UtilConfig.EMPTY_STRING;
	public final static String NO_HUB_URL = UtilConfig.EMPTY_STRING;
	public final static long NO_UPDATING_PERIOD = 0;
	
	public final static String DOCS_HOME = CenterConfig.CENTER_HOME + "Data/CrawledPages/";
	public final static String CONFIG_PATH = CenterConfig.CENTER_HOME + "Config/Crawl/";
	
	public final static int ASSIGNMENT_SIZE = 100;

	public final static HubSource NO_HUB_SOURCE = null;
	public final static int ACCESS_HUB_TIMEOUT = 50000;
	public final static long NOTIFICATION_THREAD_WAIT_TIME = 1000;
	public final static CurrentLinkKeys NO_CURRENT_LINK_KEYS = null;
	public final static Set<String> NO_LINK_KEYS = null;
	public final static String NO_HASH = "";
	public final static int CRAWLING_TIMER_PERIOD = 500;
	public final static int UPDATING_OFFSET = 500;
	public final static int MIN_UPDATING_PERIOD = 1000;

	public final static String TAG_A = "a";
	public final static String HREF = "abs:href";
	public final static Map<String, String> NO_IMAGE_LINKS = null;

	public static final int MIN_CHINESE_AUTHORITY_TITLE_LENGTH = 10;
	public static final int MIN_ENGLISH_AUTHORITY_TITLE_LENGTH = 5;
	public static final String SPACE_WORD_DELIMITER = " ";
	public final static String NO_IMAGE_URL = "";

	public final static int SOLR_AUTHORITY_PERSISTER_THREAD_POOL_SIZE = 100;
	public final static long SOLR_AUTHORITY_PERSISTER_THREAD_KEEP_ALIVE_TIME = 4000;
	public final static long SOLR_AUTHORITY_PERSISTER_THREAD_POOL_SHUTDOWN_TIMEOUT = 3000;

	public final static int MAX_SOLR_AUTHORITY_PERSISTENT_POOL_SIZE = 100;
	public final static int MAX_SOLR_AUTHORITY_PERSISTENT_TASK_SIZE = 50;
	public final static long SOLR_AUTHORITY_PERSISTENT_DISPATCHER_IDLE_CHECK_DELAY = 3000;
	public final static long SOLR_AUTHORITY_PERSISTENT_DISPATCHER_IDLE_CHECK_PERIOD = 3000;
	public final static long SOLR_AUTHORITY_PERSISTENT_DISPATCHER_WAIT_TIME = 1000;
	public final static int SOLR_AUTHORITY_PERSISTENT_DISPATCHER_WAIT_ROUND = 5;
	
	public final static int CRAWLER_FAST_POOL_SIZE = 20;
	public final static int CRAWLER_SLOW_POOL_SIZE = 5;
	public final static int CRAWLER_THREAD_TASK_SIZE = 5;
	public final static long CRAWLER_THREAD_IDLE_TIME = 30000;

	public final static long CRAWLER_IDLE_CHECK_DELAY = 5000;
	public final static long CRAWLER_IDLE_CHECK_PERIOD = 10000;
	public final static CrawlThread NO_CRAWL_THREAD = null;

	public final static int EMPTY = 1;
	public final static int NOT_EMPTY = 0;
	public final static int NOT_AVAILABLE = -1;

	public final static int IDLE = 1;
	public final static int BUSY = 0;

	public final static long HUB_CRAWL_SCHEDULER_WAIT_TIME = 2000;
	
	public final static int MAX_BUSY_TIME_LENGTH = 200;
	public final static int MAX_SLOW_TIME_LENGHT = 400;

	public final static long CRAWLER_STATE_CHECK_PERIOD = 2000;

	public final static int UPLOAD_PAGES_DISPATCHER_POOL_SIZE = 100;
	public final static int UPLOAD_AUTHORITIES_NOTIFICATION_TASK_SIZE = 8;
	public final static long UPLOAD_PAGES__DISPATCHER_IDLE_CHECK_DELAY = 3000;
	public final static long UPLOAD_PAGES__DISPATCHER_IDLE_CHECK_PERIOD = 3000;
	public final static long UPLOAD_PAGES__DISPATCHER_WAIT_TIME = 1000;
	public final static int UPLOAD_PAGES__DISPATCHER_WAIT_ROUND = 5;

	public final static int UPLOAD_AUTHORITIES_COUNT = 1000;
	public final static long UPLOAD_AUTHORITIES_CHECK_PERIOD = 3000;

	public final static int TIME_TO_WAIT_FOR_THREAD_TO_DIE = 2000;
	public final static long PAUSE_TIME = 10000;
	
	public final static String BAR = "|";
	public final static String RETURN = "\n";

	public final static String CHILD = "CrawlChild";
	public final static String CHILD_PATHS = "ChildPaths";
	public final static int CHILD_PATH_CACHE_SIZE = 100;
	public final static int CHILD_PATH_OFFHEAP_SIZE_IN_MB = 2;
	public final static int CHILD_PATH_DISK_SIZE_IN_MB = 5;
}
