package com.greatfree.testing.coordinator.crawling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.greatfree.concurrency.Threader;
import com.greatfree.testing.data.URLValue;
import com.greatfree.testing.db.DBConfig;
import com.greatfree.testing.db.URLDB;
import com.greatfree.testing.db.URLDBPool;

/*
 * This class is responsible for distributing the crawling load, URLs, to each online crawlers. In the version, it is assumed that each crawler has the identical capacity. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlCoordinator
{
	// The threader takes the instance of CrawlLoadDistributer to assign crawling URLs to each crawler. 11/25/2014, Bing Li
	private Threader<CrawlLoadDistributer, CrawlLoadDistributerDisposer> loadDistributer;
	
	private CrawlCoordinator()
	{
	}

	/*
	 * A singleton implementation. 11/25/2014, Bing Li
	 */
	private static CrawlCoordinator instance = new CrawlCoordinator();
	
	public static CrawlCoordinator COORDINATOR()
	{
		if (instance == null)
		{
			instance = new CrawlCoordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the coordinator. 11/25/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.loadDistributer.stop();
	}

	/*
	 * Initialize the threader to distribute crawling loads. 11/25/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the threader. 11/25/2014, Bing Li
		this.loadDistributer = new Threader<CrawlLoadDistributer, CrawlLoadDistributerDisposer>(new CrawlLoadDistributer(new CrawlLoadSender()), new CrawlLoadDistributerDisposer());
		// Start the threader. 11/25/2014, Bing Li
		this.loadDistributer.start();
	}

	/*
	 * Get the crawling load count. 11/25/2014, Bing Li
	 */
	public long getURLCount()
	{
		// Initialize an instance of URLDB. 11/25/2014, Bing Li
		URLDB db = URLDBPool.PERSISTENT().getDB(DBConfig.URL_DB_PATH);
		// Get the count of all of URLs. 11/25/2014, Bing Li
		long count = db.loadAllURLCount();
		// Collect the instance of URLDB. 11/25/2014, Bing Li
		URLDBPool.PERSISTENT().collectDB(db);
		// Return the count of all of the URLs. 11/25/2014, Bing Li
		return count;
	}

	/*
	 * Distribute the crawling load to each crawler. 11/25/2014, Bing Li
	 */
	public void distributeCrawlLoads()
	{
		// Define an instance of ArryList that contains all of the crawler keys. 11/25/2014, Bing Li
		List<String> dcKeys = new ArrayList<String>(CrawlRegistry.COORDINATE().getCrawlDCKeys());
		// Check whether the count of crawlers is greater than zero. 11/25/2014, Bing Li
		if (dcKeys.size() > 0)
		{
			// Get an instance of URLDB. 11/25/2014, Bing Li
			URLDB db = URLDBPool.PERSISTENT().getDB(DBConfig.URL_DB_PATH);
			// Load all of the URLs to be crawled. 11/25/2014, Bing Li
			Map<String, URLValue> allURLs = db.loadAllURLs();
			// Collect the instance of URLDB. 11/25/2014, Bing Li
			URLDBPool.PERSISTENT().collectDB(db);
			// Initialize a collection to take a certain number of URLs, which must be assigned to one of the crawler. 11/26/2014, Bing Li
			HashMap<String, URLValue> urls = new HashMap<String, URLValue>();
			// Check whether the count of crawlers is less than that of the URLs to be crawled. 11/26/2014, Bing Li
			if (dcKeys.size() <= allURLs.size())
			{
				// Usually, the count of crawlers is much less than that of the URLs to be crawled. If so, estimate how many URLs each crawler needs to take, the value of task load. 11/26/2014, Bing Li
				int taskLoad = allURLs.size() / dcKeys.size() + 1;
				// Initialize an index that is a number to select a crawler from the list containing crawlers. 11/26/2014, Bing Li
				int index = 0;
				// Scan all of the URLs to assign them to each crawler. 11/26/2014, Bing Li
				for (URLValue url : allURLs.values())
				{
					// Put one URL into a list. 11/26/2014, Bing Li
					urls.put(url.getKey(), url);
					// Check whether the size of the list is greater than the task load each crawler needs to take. 11/26/2014, Bing Li
					if (urls.size() >= taskLoad)
					{
						// Put the crawling load into the load distributer, which is responsible for sending the URLs to the crawler indicated by the index. 11/26/2014, Bing Li
						this.loadDistributer.getFunction().produce(new CrawlLoad(dcKeys.get(index), urls));
						// Increment the index. 11/26/2014, Bing Li
						index++;
						// Initialize a collection to take the rest collection. 11/26/2014, Bing Li
						urls = new HashMap<String, URLValue>();
					}
				}
				
				// When the above quits, it is necessary to check whether the collection contains some URLs and whether some crawlers has not got any URLs to crawl. 11/26/2014, Bing Li 
				if (urls.size() > 0 && index < dcKeys.size())
				{
					// Put the crawling load into the load distributer, which is responsible for sending the URLs to the crawler indicated by the index. 11/26/2014, Bing Li
					this.loadDistributer.getFunction().produce(new CrawlLoad(dcKeys.get(index), urls));
				}
			}
			else
			{
				// Usually, the count of crawlers is much less than that of the URLs to be crawled. If not, just send all of the URLs to the first crawler indicated by the index. 11/26/2014, Bing Li
				this.loadDistributer.getFunction().produce(new CrawlLoad(dcKeys.get(0), urls));
			}
			// Set the flag that all of the URLs are assigned. Then, the load distributer must end after all of the loads are sent to the crawlers respectively. 11/26/2014, Bing Li
			this.loadDistributer.getFunction().setIsFoodQueued(true);
		}
	}
}
