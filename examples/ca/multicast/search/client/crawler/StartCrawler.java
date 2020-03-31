package ca.multicast.search.client.crawler;

import java.io.IOException;
import java.util.List;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.FileManager;

import ca.multicast.search.message.CrawlDataNotification;
import ca.multicast.search.message.SearchConfig;

// Created: 03/16/2020, Bing Li
class StartCrawler
{

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		List<String> texts = FileManager.readText(SearchConfig.TEST_DATA);
		
		Crawler.FRONT().init();
		for (int i = 0; i < texts.size(); i++)
		{
			System.out.println("Crawled data: " + texts.get(i));
			Crawler.FRONT().syncNotify(new CrawlDataNotification(texts.get(i)));
			Thread.sleep(SearchConfig.TIMEOUT);
		}
		Crawler.FRONT().dispose();
	}

}
