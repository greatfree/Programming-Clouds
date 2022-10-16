package org.greatfree.app.search.cs.multinode.client.crawler;

import java.io.IOException;

import org.greatfree.app.search.multicast.message.CrawledPagesNotification;
import org.greatfree.app.search.multicast.message.Page;
import org.greatfree.exceptions.RemoteReadException;

// Created: 10/08/2018, Bing Li
class StartCrawler
{

	public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException
	{
		System.out.println("Crawler starting up ...");

		try
		{
			Crawler.FRONT().init();
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Crawler started ...");

		Page page;
		int n = 0;
		for (int i = 0; i < 50; i++)
		{
			n = i % 2;
			if (n == 0)
			{
				page = new Page("Java Programming" + i, "Programming large-scale distributed systems ...", "http://www.cpu" + i + ".com/page" + i, true);
			}
			else
			{
				page = new Page("C++ Programming" + i, "Programming embedded systems ...", "http://www.memory" + i + ".com/page" + i, false);
			}
			Crawler.FRONT().asyncNotify(new CrawledPagesNotification(page));
			Thread.sleep(2000);
		}
		
		Crawler.FRONT().dispose();
	}

}
