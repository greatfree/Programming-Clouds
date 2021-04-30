package edu.chainnet.crawler.child.crawl;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimerTask;

import ca.mama.util.Time;
import edu.chainnet.crawler.CrawlConfig;

// Created: 04/24/2021, Bing Li
class CrawlerStateChecker extends TimerTask
{

	@Override
	public void run()
	{
//		Set<String> fastCrawlThreadKeys = CrawlScheduler.CANADA().getFastCrawlThreadKeys();
		Set<String> fastCrawlThreadKeys = CrawlScheduler.CRAWL().getFastThreadKeys();

		System.out.println("\n============= Fast Crawler =============");
		System.out.println("Current time: " + Calendar.getInstance().getTime());
		System.out.println("------> Fast crawler size: " + fastCrawlThreadKeys.size());
		Date startTime;
		for (String key : fastCrawlThreadKeys)
		{
			startTime = CrawlScheduler.CRAWL().getFastStartTime(key);
			if (startTime != Time.INIT_TIME)
			{
				if (Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) >= CrawlConfig.MAX_BUSY_TIME_LENGTH)
				{
					System.out.println("Aalleviating to Slow: " + CrawlScheduler.CRAWL().getFastTask(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) + " seconds");
					try
					{
						CrawlScheduler.CRAWL().alleviateSlow(key);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("Working: " + CrawlScheduler.CRAWL().getFastTask(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) + " seconds");
				}
			}
			else
			{
				System.out.println("Done: " + CrawlScheduler.CRAWL().getFastTask(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), CrawlScheduler.CRAWL().getFastEndTime(key)) + " seconds");
				int isIdle = CrawlScheduler.CRAWL().isIdle(key);
				if (isIdle == CrawlConfig.IDLE)
				{
					System.out.println("The thread is idle at " + CrawlScheduler.CRAWL().getIdleTime(key));
				}
				else if (isIdle == CrawlConfig.BUSY)
				{
					System.out.println("The thread is NOT idle");
				}
				else
				{
					System.out.println("The thread is NOT existed");
				}

				try
				{
					int isEmpty = CrawlScheduler.CRAWL().isFastEmpty(key);
					if (isEmpty == 1)
					{
						System.out.println("The relevant queue is empty");
					}
					else if (isEmpty == 0)
					{
						System.out.println("The relevant queue is NOT empty");
					}
					else
					{
						System.out.println("The relevant queue is NOT existed");
					}
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		System.out.println("========================================\n");

		System.out.println("\n============ Slow Crawler ==============");
		try
		{
			CrawlScheduler.CRAWL().appendSlow();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		Set<String> slowCrawlThreadKeys = CrawlScheduler.CRAWL().getSlowThreadKeys();
		System.out.println("Current time: " + Calendar.getInstance().getTime());
		System.out.println("------> Slow crawler size: " + slowCrawlThreadKeys.size());
		for (String key : slowCrawlThreadKeys)
		{
			startTime = CrawlScheduler.CRAWL().getSlowStartTime(key);
			if (startTime != Time.INIT_TIME)
			{
				System.out.println("Working: " + CrawlScheduler.CRAWL().getSlowTask(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) + " seconds");
				if (Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), startTime) >= CrawlConfig.MAX_SLOW_TIME_LENGHT)
				{
					try
					{
						CrawlScheduler.CRAWL().killSlow(key);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			else
			{
				System.out.println("Accomplished: " + CrawlScheduler.CRAWL().getSlowTask(key) + " takes " + Time.getTimeSpanInSecond(Calendar.getInstance().getTime(), CrawlScheduler.CRAWL().getSlowEndTime(key)) + " seconds");
			}
		}
		System.out.println("========================================\n");
	}

}
