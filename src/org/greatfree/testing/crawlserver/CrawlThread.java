package org.greatfree.testing.crawlserver;

import org.greatfree.concurrency.reactive.InteractiveQueue;
import org.greatfree.data.ServerConfig;

/*
 * This is the thread that takes the task of crawling concurrently. It is derived from InteractiveQueue. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlThread extends InteractiveQueue<HubURL, CrawlNotifier>
{
	/*
	 * Initialize the crawling thread. 11/23/2014, Bing Li
	 */
	public CrawlThread(int taskSize, CrawlNotifier notifier)
	{
		super(taskSize, notifier);
	}

	/*
	 * Dispose the thread. 11/23/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		super.dispose();
	}

	/*
	 * Expose the URL being crawled. 11/23/2014, Bing Li
	 */
	public String getURL()
	{
		// Get the current URL being crawled. 11/23/2014, Bing Li
		HubURL url = this.getCurrentTask();
		// Check whether the URL is valid. 11/23/2014, Bing Li
		if (url != CrawlConfig.NO_URL)
		{
			// Return the URL. 11/23/2014, Bing Li
			return url.getURL();
		}
		// Return null if no crawling URLs are available. 11/23/2014, Bing Li
		return CrawlConfig.NO_URL_LINK;
	}

	/*
	 * Crawl each URL scheduled to the thread one by one concurrently. 11/23/2014, Bing Li
	 */
	public void run()
	{
		// An instance of HubURL. 11/23/2014, Bing Li
		HubURL url = CrawlConfig.NO_URL;
		// Check whether the crawling thread is terminated. 11/23/2014, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the task queue is empty. 11/23/2014, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// If the queue is not empty, dequeue the instance of URL. 11/23/2014, Bing Li
					url = this.getTask();
					// Set the starting time stamp. 11/23/2014, Bing Li
					this.setStartTime();
					// Crawl the dequeued URL. 11/23/2014, Bing Li
					Crawler.crawl(url);
					// Notify the interactive dispatcher that the URL is crawled. 11/23/2014, Bing Li
					this.done(url);
					// Set the ending time stamp. 11/23/2014, Bing Li
					this.setEndTime();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for some time if the task queue is empty. 11/23/2014, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getWorkload()
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
