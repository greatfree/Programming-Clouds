package edu.chainnet.crawler.coordinator;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.crawler.CrawlConfig;

// Created: 04/22/2021, Bing Li
class StartCrawler
{
	public static void main(String[] args)
	{
		System.out.println("Crawling Coordinator starting up ...");

		try
		{
			CrawlCoordinator.CRAWL().start(CrawlConfig.CRAWL_COODINATOR_PORT, new CoordinatorTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}

		System.out.println("Crawling Coordinator started ...");

		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

