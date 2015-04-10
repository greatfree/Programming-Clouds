package com.greatfree.testing.crawlserver;

import java.util.Set;

import com.greatfree.testing.data.CrawledLink;
import com.greatfree.testing.db.DBConfig;

/*
 * The class contains the constants and configurations for the crawler. 11/28/2014, Bing Li
 */

// Created: 11/11/2014, Bing Li
public class CrawlConfig
{
	public final static int CRAWLING_TASK_LISTENER_THREAD_POOL_SIZE = 100;
	public final static long CRAWLING_TASK_LISTENER_THREAD_ALIVE_TIME = 10000;

	public final static HubURL NO_URL = null;
	public final static String NO_URL_LINK = "";

	public final static long LOCK_TIME_OUT = 0;

	public final static int CRAWLING_TIMER_PERIOD = 5;
	public final static int UPDATING_VALUE = 5;
	public final static int MIN_UPDATING_PERIOD = 1000;

	public final static String CRAWLING_DB_ROOT = DBConfig.DB_HOME + "CrawlDB/";
	public final static String CRAWLED_FILE_PATH = CrawlConfig.CRAWLING_DB_ROOT + "HubCrawledFiles/";
	
	public final static Set<CrawledLink> NO_LINK_KEYS = null;

	public final static int CRAW_TIMEOUT = 50000;

	public final static String TAG_A = "a";
	public final static String HREF = "href";

	public final static int CRAWLER_FAST_POOL_SIZE = 320;
	public final static int CRAWLER_SLOW_POOL_SIZE = 30;
	public final static int CRAWLER_THREAD_TASK_SIZE = 5;
	public final static long CRAWLER_THREAD_IDLE_TIME = 30000;
	public final static long CRAWLER_IDLE_CHECK_DELAY = 5000;
	public final static long CRAWLER_IDLE_CHECK_PERIOD = 10000;

	public final static CrawlThread NO_CRAWL_THREAD = null;
	
	public final static int IDLE = 1;
	public final static int BUSY = 0;
	public final static int DEAD = -1;

	public final static long CRAWL_SCHEDULER_WAIT_TIME = 2000;

	public final static int SUB_CLIENT_POOL_SIZE = 500;
	public final static long SUB_CLIENT_IDLE_CHECK_DELAY = 3000;
	public final static long SUB_CLIENT_IDLE_CHECK_PERIOD = 3000;
	public final static long SUB_CLIENT_MAX_IDLE_TIME = 3000;

	public final static int MAX_BUSY_TIME_LENGTH = 200;
	
	public final static int EMPTY = 1;
	public final static int NOT_EMPTY = 0;

	public final static long CRAWLING_STATE_CHECK_PERIOD = 2000;
}
