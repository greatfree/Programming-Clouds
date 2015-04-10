package com.greatfree.testing.crawlserver;

import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.TerminateSignal;

/*
 * This is the unique entry and exit for the crawling server. 11/28/2014, Bing Li
 */

// Created: 11/11/2014, Bing Li
public class StartCrawler
{
	public static void main(String[] args)
	{
		// Start the crawling server. 11/28/2014, Bing Li
		CrawlServer.CRAWL().start(ServerConfig.CRAWL_SERVER_PORT);
		// Detect whether the process is shutdown. 11/28/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// Sleep for some time if the process is not shutdown. 11/28/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
