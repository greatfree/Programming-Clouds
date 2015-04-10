package com.greatfree.testing.crawlserver;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimerTask;

import com.greatfree.util.Time;
import com.greatfree.util.UtilConfig;

/*
 * The class is responsible for checking the states of all of the currently working crawling threads. Each thread has some workloads to crawl the URLs. However, for the complicated circumstance of the Web, some URLs might be slow. Some might be fast. Some might be dead. It must affect the scheduling the tasks if some threads work on slow and dead URLs. Those threads must identified by the checker. If necessary, their workloads must be rescheduled in order to save the resources. Some interactions between those threads and the interactive dispatchers are required to happen in the checker. 11/27/2014, Bing Li
 * 
 * The checker must work periodically. So it is derived from TimerTask. 11/27/2014, Bing Li
 * 
 * In addition, the checker also displays the states on the screen for possible monitors of administrators. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class CrawlingStateChecker extends TimerTask
{
	/*
	 * The checking is performed concurrently by a timer. 11/27/2014, Bing Li
	 */
	@Override
	public void run()
	{
		// Get the fast crawling thread keys. 11/27/2014, Bing Li
		Set<String> fastCrawlThreadKeys = CrawlScheduler.CRAWL().getFastCrawlThreadKeys();

		// Display the necessary information on the screen. 11/27/2014, Bing Li
		System.out.println("\n============= Fast Crawler =============");
		// Display the current time. 11/27/2014, Bing Li
		System.out.println("Current time: " + Calendar.getInstance().getTime());
		// Display the size of the fast threads. 11/27/2014, Bing Li
		System.out.println("------> Fast crawler size: " + fastCrawlThreadKeys.size());
		// Declare the starting time of crawling for threads. 11/27/2014, Bing Li
		Date startTime;
		// Scan each thread to check their states. 11/27/2014, Bing Li
		for (String key : fastCrawlThreadKeys)
		{
			// Get the starting time of one particular fast thread by its key. 11/27/2014, Bing Li
			startTime = CrawlScheduler.CRAWL().getFastStartTime(key);
			// Check whether the starting time is valid or not. 11/27/2014, Bing Li
			if (startTime != UtilConfig.NO_TIME)
			{
				// Check whether the starting time is a meaningless value. 11/27/2014, Bing Li
				if (startTime != Time.INIT_TIME)
				{
					// Check whether the fast thread has been starting crawling for long enough to exceed the upper limit. 11/27/2014, Bing Li
					if (Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) >= CrawlConfig.MAX_BUSY_TIME_LENGTH)
					{
						System.out.println("Killed: " + CrawlScheduler.CRAWL().getFastURL(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) + " seconds");
						try
						{
							// If so, the fast thread must be turned to a slow one. 11/27/2014, Bing Li
							CrawlScheduler.CRAWL().alleviateSlow(key);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						System.out.println("Working: " + CrawlScheduler.CRAWL().getFastURL(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) + " seconds");
					}
				}
				else
				{
					System.out.println("------------------------------------");
					// If the starting time is meaningless, it denotes that the thread has no job to do. 11/27/2014, Bing Li
					System.out.println("Done: " + CrawlScheduler.CRAWL().getFastURL(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), CrawlScheduler.CRAWL().getFastEndTime(key)) + " seconds");
				}
			}
		}
		System.out.println("========================================\n");

		System.out.println("\n============ Slow Crawler ==============");
		// Get the slow thread keys. 11/27/2014, Bing Li
		Set<String> slowCrawlThreadKeys = CrawlScheduler.CRAWL().getSlowCrawlThreadKeys();
		System.out.println("Current time: " + Calendar.getInstance().getTime());
		// Display the size of the slow threads. 11/27/2014, Bing Li
		System.out.println("------> Slow crawler size: " + slowCrawlThreadKeys.size());
		// Scan each slow thread to check their states. 11/27/2014, Bing Li
		for (String key : slowCrawlThreadKeys)
		{
			// Get the starting time of one particular slow thread. 11/27/2014, Bing Li
			startTime = CrawlScheduler.CRAWL().getSlowStartTime(key);
			// Check whether the starting time is valid. 11/27/2014, Bing Li
			if (startTime != UtilConfig.NO_TIME)
			{
				// Check whether the starting time make sense. 11/27/2014, Bing Li
				if (startTime != Time.INIT_TIME)
				{
					// If the starting time is effective, display the time the slow thread work on the specific URL. 11/27/2014, Bing Li
					System.out.println("Working: " + CrawlScheduler.CRAWL().getSlowURL(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) + " seconds");
				}
				else
				{
					// If the starting time does not make sense, it denotes that the thread has finished its task. Display the consumed time. 11/27/2014, Bing Li
					System.out.println("Accomplished: " + CrawlScheduler.CRAWL().getSlowURL(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), CrawlScheduler.CRAWL().getSlowEndTime(key)) + " seconds");
				}
			}
		}
		System.out.println("========================================\n");
	}
}
