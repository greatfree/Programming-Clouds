package org.greatfree.testing.crawlserver;

import java.util.Date;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.greatfree.concurrency.reactive.InteractiveDispatcher;
import org.greatfree.util.FileManager;
import org.greatfree.util.Time;
import org.greatfree.util.UtilConfig;

/*
 * This is the crawling scheduler. It works on the way defined in InteractiveDispatcher. 11/23/2014, Bing Li
 */

// Created: 11/21/2014, Bing Li
public class CrawlScheduler
{
	// The interactive dispatcher is responsible for creating instances of crawling threads and interact with them to adjust the crawling procedure to reach the goal of the high performance crawling. 11/23/2014, Bing Li
	private InteractiveDispatcher<HubURL, CrawlNotifier, CrawlThread, CrawlThreadCreator, CrawlThreadDisposer> dispatcher;
	// The queue to take all of the URLs to be crawled. Since each URL needs to be crawled periodically, the queue is defined to schedule them in a FIFO way infinitely. 11/23/2014, Bing Li
	private Queue<HubURL> urlQueue;
	
	private CrawlScheduler()
	{
	}

	/*
	 * A singleton implementation. 11/23/2014, Bing Li
	 */
	private static CrawlScheduler instance = new CrawlScheduler();
	
	public static CrawlScheduler CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlScheduler();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the scheduler. 11/23/2014, Bing Li
	 */
	public void dispose()
	{
		try
		{
			// Clear the queue. 11/23/2014, Bing Li
			this.urlQueue.clear();
			// Dispose the interactive dispatcher. 11/23/2014, Bing Li
			this.dispatcher.dispose();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Initialize the interactive dispatcher. 11/23/2014, Bing Li
	 */
	public void init()
	{
		/*
		 * A directory on the hard disk is required to save files for crawling. 11/23/2014, Bing Li
		 */
		// Check whether the required directory exists. 11/23/2014, Bing Li
		if (!FileManager.isDirExisted(CrawlConfig.CRAWLED_FILE_PATH))
		{
			// Create the directory if it does not exist. 11/23/2014, Bing Li
			FileManager.makeDir(CrawlConfig.CRAWLED_FILE_PATH);
		}

		// Initialize the queue. 11/23/2014, Bing Li
		this.urlQueue = new LinkedBlockingQueue<HubURL>();

		// Initialize the interactive dispatcher. 11/23/2014, Bing Li
		this.dispatcher = new InteractiveDispatcher<HubURL, CrawlNotifier, CrawlThread, CrawlThreadCreator, CrawlThreadDisposer>(CrawlConfig.CRAWLER_FAST_POOL_SIZE, CrawlConfig.CRAWLER_SLOW_POOL_SIZE, CrawlConfig.CRAWLER_THREAD_TASK_SIZE, CrawlConfig.CRAWLER_THREAD_IDLE_TIME, new CrawlNotifier(), new CrawlThreadCreator(), new CrawlThreadDisposer());
		// Set the idle checking for the interactive dispatcher. 11/23/2014, Bing Li
		this.dispatcher.setIdleChecker(CrawlConfig.CRAWLER_IDLE_CHECK_DELAY, CrawlConfig.CRAWLER_IDLE_CHECK_PERIOD);
	}

	/*
	 * Check whether the interactive dispatcher is shutdown. 11/23/2014, Bing Li
	 */
	public boolean isShutdown()
	{
		return this.dispatcher.isShutdown();
	}

	/*
	 * Check whether the interactive dispatcher is holding on. 11/23/2014, Bing Li
	 */
	public boolean isPaused()
	{
		return this.dispatcher.isPaused();
	}

	/*
	 * Demand the interactive dispatcher to hold on. 11/23/2014, Bing Li
	 */
	public void holdOn() throws InterruptedException
	{
		this.dispatcher.holdOn();
	}

	/*
	 * Demand the interactive dispatcher to keep on working. 11/23/2014, Bing Li
	 */
	public void keepOn()
	{
		this.dispatcher.keepOn();
	}

	/*
	 * Get the keys of fast crawling threads. 11/23/2014, Bing Li
	 */
	public Set<String> getFastCrawlThreadKeys()
	{
		return this.dispatcher.getFastThreadKeys();
	}

	/*
	 * Get the starting time of a specific fast crawling thread by its key. 11/23/2014, Bing Li
	 */
	public Date getFastStartTime(String key)
	{
		// Get the specific fast thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getFastFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Return the starting time if the thread is valid. 11/23/2014, Bing Li
			return thread.getStartTime();
		}
		// Return null if the thread is invalid. 11/23/2014, Bing Li
		return UtilConfig.NO_TIME;
	}

	/*
	 * Get the ending time of a specific fast crawling by its key. 11/23/2014, Bing Li
	 */
	public Date getFastEndTime(String key)
	{
		// Get the specific fast thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getFastFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Return the ending time if the thread is valid. 11/23/2014, Bing Li
			return thread.getEndTime();
		}
		// Return null if the thread is invalid. 11/23/2014, Bing Li
		return UtilConfig.NO_TIME;
	}

	/*
	 * Get the URL which a specific fast thread is working on. 11/23/2014, Bing Li
	 */
	public String getFastURL(String key)
	{
		// Get the specific fast thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getFastFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Return the URL if the thread is valid. 11/23/2014, Bing Li
			return thread.getURL();
		}
		// Return null if the thread is invalid. 11/23/2014, Bing Li
		return CrawlConfig.NO_URL_LINK;
	}
	
	/*
	 * Get the starting time of a specific slow crawling thread by its key. 11/23/2014, Bing Li
	 */
	public Date getSlowStartTime(String key)
	{
		// Get the specific slow thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getSlowFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Return the starting time if the thread is valid. 11/23/2014, Bing Li
			return thread.getStartTime();
		}
		// Return null if the thread is invalid. 11/23/2014, Bing Li
		return UtilConfig.NO_TIME;
	}
	
	/*
	 * Get the ending time of a specific slow crawling by its key. 11/23/2014, Bing Li
	 */
	public Date getSlowEndTime(String key)
	{
		// Get the specific slow thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getSlowFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Return the ending time if the thread is valid. 11/23/2014, Bing Li
			return thread.getEndTime();
		}
		// Return null if the thread is invalid. 11/23/2014, Bing Li
		return UtilConfig.NO_TIME;
	}
	
	/*
	 * Get the URL which a specific slow thread is working on. 11/23/2014, Bing Li
	 */
	public String getSlowURL(String key)
	{
		// Get the specific slow thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getSlowFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Return the URL if the thread is valid. 11/23/2014, Bing Li
			return thread.getURL();
		}
		// Return null if the thread is invalid. 11/23/2014, Bing Li
		return CrawlConfig.NO_URL_LINK;
	}

	/*
	 * Check whether a fast thread's task queue is empty. 11/27/2014, Bing Li
	 */
	public int IsFastEmpty(String key)
	{
		// Get the specific fast thread. 11/27/2014, Bing Li
		CrawlThread thread = this.dispatcher.getFastFunction(key);
		// Check whether the thread is valid. 11/27/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Check whether the thread is empty. 11/27/2014, Bing Li
			if (thread.isEmpty())
			{
				// Return empty. 11/27/2014, Bing Li
				return CrawlConfig.EMPTY;
			}
			else
			{
				// Return not empty. 11/27/2014, Bing Li
				return CrawlConfig.NOT_EMPTY;
			}
		}
		// Return dead. 11/27/2014, Bing Li
		return CrawlConfig.DEAD;
	}

	/*
	 * Check whether a fast crawling thread is idle or not. 11/23/2014, Bing Li
	 */
	public int isIdle(String key)
	{
		// Get the specific fast thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getFastFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			// Check whether the thread is idle. 11/23/2014, Bing Li
			if (thread.getIdleTime() != Time.INIT_TIME)
			{
				// Return idle if it is. 11/23/2014, Bing Li
				return CrawlConfig.IDLE;
			}
			else
			{
				// Return busy if it is not. 11/23/2014, Bing Li
				return CrawlConfig.BUSY;
			}
		}
		// Return dead it does not exist. 11/23/2014, Bing Li
		return CrawlConfig.DEAD;
	}

	/*
	 * Get the idle time of a fast crawling thread. 11/23/2014, Bing Li
	 */
	public Date getIdleTime(String key)
	{
		// Get the specific fast thread. 11/23/2014, Bing Li
		CrawlThread thread = this.dispatcher.getFastFunction(key);
		// Check whether the thread is valid. 11/23/2014, Bing Li
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{		
			// Check whether the thread is idle. 11/23/2014, Bing Li
			if (thread.getIdleTime() != Time.INIT_TIME)
			{
				// Return the idle time if it is. 11/23/2014, Bing Li
				return thread.getIdleTime();
			}
			else
			{
				// Return a meaningless time if it is not. 11/23/2014, Bing Li
				return Time.INIT_TIME;
			}
		}
		// Return a meaningless time if it does not exist. 11/23/2014, Bing Li
		return Time.INIT_TIME;
	}

	/*
	 * Get the keys of slow crawling threads. 11/23/2014, Bing Li
	 */
	public Set<String> getSlowCrawlThreadKeys()
	{
		return this.dispatcher.getSlowThreadKeys();
	}

	/*
	 * Solve the problem if a thread is detected slow. 11/23/2014, Bing Li
	 */
	public synchronized void alleviateSlow(String key) throws InterruptedException
	{
		this.dispatcher.alleviateSlow(key);
	}

	/*
	 * Restore the slow thread to fast if applicable. 11/23/2014, Bing Li
	 */
	public synchronized void restoreFast(String key) throws InterruptedException
	{
		this.dispatcher.restoreFast(key);
	}

	/*
	 * Expose the URL count to be crawled. 11/23/2014, Bing Li
	 */
	public long getURLCount()
	{
		return this.urlQueue.size();
	}

	/*
	 * Enqueue a hub URL for scheduling. 11/23/2014, Bing Li
	 */
	public void enqueue(HubURL hub)
	{
		this.urlQueue.add(hub);
	}

	/*
	 * Submit a hub URL for crawling. 11/23/2014, Bing Li
	 */
	public void submit() throws InterruptedException
	{
		// Dequeue a hub URL from the queue. 11/23/2014, Bing Li
		HubURL hub = this.urlQueue.poll();
		// Check whether hub URL is valid. 11/23/2014, Bing Li
		if (hub != CrawlConfig.NO_URL)
		{
			// Check whether the passed time of the hub URL is longer than its updating period. 11/23/2014, Bing Li
			if (hub.getPassedTime() >= hub.getUpdatingPeriod())
			{
				// If so, it denotes that it is time to crawl it. Thus, the hub URL is enqueued into the interactive dispatcher. 11/23/2014, Bing Li
				this.dispatcher.enqueue(hub);
			}
			else
			{
				// If not, it denotes that it is not the proper moment to crawl the hub because it might not be updated. 11/23/2014, Bing Li
				hub.incrementTime();
				// Enqueue the hub URL to the end of the queue for future scheduling. 11/23/2014, Bing Li
				this.urlQueue.add(hub);
			}
		}
	}
}
