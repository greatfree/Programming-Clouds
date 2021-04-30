package edu.chainnet.crawler.child.crawl;

import edu.chainnet.center.CenterConfig;

// Created: 04/24/2021, Bing Li
public class CrawlCacheConfig
{
	public final static String CACHE_HOME = CenterConfig.CENTER_HOME + "Data/Cache/";

	public final static String HUB_HANDLE_MAP_CACHE_KEY = "HubHandleMap";
	
	// The below constant is too large and it might cause the exception of out-of-memory. 11/20/2017, Bing Li
//	public final static int HUB_HANDLE_MAP_CACHE_SIZE = 500000;
	public final static int HUB_HANDLE_MAP_CACHE_SIZE = 1000;
	public final static int HUB_HANDLE_MAP_CACHE_OFFHEAP_SIZE_IN_MB = 500;
	public final static int HUB_HANDLE_MAP_CACHE_DISK_SIZE_IN_MB = 1000;
	public final static int HUB_HANDLE_MAP_EVICTED_COUNT_ALERT = 2000;
	
	public final static String GROUPED_HUB_KEYS_MAP_CACHE_KEY = "GroupedHubKeysMap";
	// The below constant is too large and it might cause the exception of out-of-memory. 11/20/2017, Bing Li
//	public final static int GROUPED_HUB_KEYS_MAP_CACHE_SIZE = 5000;
	public final static int GROUPED_HUB_KEYS_MAP_CACHE_SIZE = 800;
	public final static int GROUPED_HUB_KEYS_MAP_CACHE_OFFHEAP_SIZE_IN_MB = 100;
	public final static int GROUPED_HUB_KEYS_MAP_CACHE_DISK_SIZE_IN_MB = 500;
	public final static int GROUPED_HUB_KEYS_MAP_EVICTED_COUNT_ALERT = 2000;
	
	public final static String AUTHORITY_SOLR_QUEUE_CACHE_KEY = "AuthoritySolrQueue";
	// The below constant is too large and it might cause the exception of out-of-memory. 11/20/2017, Bing Li
//	public final static int AUTHORITY_SOLR_QUEUE_CACHE_SIZE = 50000;
	public final static int AUTHORITY_SOLR_QUEUE_CACHE_SIZE = 1000;
	public final static int AUTHORITY_SOLR_QUEUE_CACHE_OFFHEAP_SIZE_IN_MB = 500;
	public final static int AUTHORITY_SOLR_QUEUE_CACHE_DISK_SIZE_IN_MB = 1000;
	
	public final static String IS_CRAWL_DONE_MAP_CACHE_KEY = "IsCrawlDoneMap";
	// The below constant is too large and it might cause the exception of out-of-memory. 11/20/2017, Bing Li
//	public final static int IS_CRAWL_DONE_MAP_CACHE_SIZE = 2000; // Currently, the total number is about 12000. 10/24/2017, Bing Li
	public final static int IS_CRAWL_DONE_MAP_CACHE_SIZE = 500; // Currently, the total number is about 12000. 10/24/2017, Bing Li
	public final static int IS_CRAWL_DONE_MAP_CACHE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int IS_CRAWL_DONE_MAP_CACHE_DISK_SIZE_IN_MB = 100;

	public final static String HUB_QUEUE_CACHE_KEY = "HubQueue";
//	public final static int HUB_QUEUE_CACHE_SIZE = 2000;
	public final static int HUB_QUEUE_CACHE_SIZE = 1000;
	public final static int HUB_QUEUE_CACHE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int HUB_QUEUE_CACHE_DISK_SIZE_IN_MB = 100;
}
