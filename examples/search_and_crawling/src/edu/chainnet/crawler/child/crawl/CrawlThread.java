package edu.chainnet.crawler.child.crawl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.RejectedExecutionException;

import org.greatfree.concurrency.batch.BalancedQueue;
import org.greatfree.util.ServerStatus;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.HubSource;

// Created: 04/24/2021, Bing Li
public class CrawlThread extends BalancedQueue<HubSource, CrawlNotifier>
{
	public CrawlThread(int taskSize, CrawlNotifier notifier)
	{
		super(taskSize, notifier);
	}

	@Override
	public String getTaskInString()
	{
		HubSource hub = this.getCurrentTask();
		if (hub != CrawlConfig.NO_HUB_SOURCE)
		{
			return hub.getHubURL();
		}
		return CrawlConfig.NO_HUB_URL;
	}

	@Override
	public void dispose() throws InterruptedException
	{
		super.shutdown();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
	}

	@Override
	public int getWorkload()
	{
		return 0;
	}


	@Override
	public void run()
	{
		HubSource hub = CrawlConfig.NO_HUB_SOURCE;
		while (!this.isShutdown())
		{
			try
			{
				while (!this.isEmpty())
				{
					try
					{
						hub = this.getTask();
						super.setStartTime();
						Crawler.crawl(hub, CrawlConfig.ACCESS_HUB_TIMEOUT);
						super.done(hub);
						super.setEndTime();
					}
					catch (InterruptedException | InstantiationException | IllegalAccessException | IllegalStateException | RejectedExecutionException | SocketTimeoutException e)
					{
//						ServerStatus.FREE().printException(e);
						CrawlPrompts.print(e);
					}
					catch (IOException e)
					{
//						System.out.println("*******************************************************");
//						ServerStatus.FREE().printException(CrawlPrompts.CONNECTION_RESET + hub.getHubURL());
						CrawlPrompts.print(e);
//						System.out.println("*******************************************************");
					}
				}
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
			try
			{
				this.holdOn(CrawlConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
//		System.out.println(super.getKey() + " for " + hub.getHubURL() + " WAS KILLED!!!!");
	}
}

