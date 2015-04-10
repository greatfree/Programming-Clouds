package com.greatfree.testing.crawlserver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.Sets;
import com.greatfree.testing.data.CrawledLink;
import com.greatfree.util.FileManager;
import com.greatfree.util.Tools;
import com.greatfree.util.UtilConfig;

/*
 * This is the class that crawls a URL. For the sample, it just gets the links from the URL, no further accessing on those links. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class Crawler
{
	/*
	 * Crawl the URL contained in the instance of HubURL. 11/23/2014, Bing Li
	 */
	public static Set<CrawledLink> crawl(HubURL hubURL)
	{
		// Create the file name, which is formed mainly by the URL key. The file saves the HTML file crawled from the URL. 11/23/2014, Bing Li
		String fileName = CrawlConfig.CRAWLED_FILE_PATH + hubURL.getKey();
		// The instance of URL, which is defined by JDK. 11/23/2014, Bing Li
		URL url;
		try
		{
			// Initialize the instance of URL. 11/23/2014, Bing Li
			url = new URL(hubURL.getURL());
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			return CrawlConfig.NO_LINK_KEYS;
		}

		// The instance of Document, which is used to take the parsed the HTML information. 11/23/2014, Bing Li
		Document doc;
		try
		{
			// Assign the value of the instance of Document. The value is parsed by JSoup. 11/23/2014, Bing Li
			doc = Jsoup.parse(url, CrawlConfig.CRAW_TIMEOUT);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return CrawlConfig.NO_LINK_KEYS;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			return CrawlConfig.NO_LINK_KEYS;
		}
		try
		{
			// Save the HTML text into the file system. 11/23/2014, Bing Li
			FileManager.createTextFile(fileName, doc.html());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return CrawlConfig.NO_LINK_KEYS;
		}
		// Create an instance of File upon the HTML text file. 11/23/2014, Bing Li
		File html = new File(fileName);
		// The instance of Document, which is used to take the parsed the HTML information. 11/23/2014, Bing Li
		Document loadedDoc;
		try
		{
			// Assign the value of the instance of Document. The value is parsed by JSoup. 11/23/2014, Bing Li
			loadedDoc = Jsoup.parse(html, UtilConfig.UTF_8, hubURL.getURL());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return CrawlConfig.NO_LINK_KEYS;
		}
		// Get the links by parsing the HTML text on the tag, "a". 11/23/2014, Bing Li
		Elements links = loadedDoc.getElementsByTag(CrawlConfig.TAG_A);
		// The link string. 11/23/2014, Bing Li
		String linkHref;
		// Initialize a set to contain all the link strings. 11/23/2014, Bing Li
		Set<CrawledLink> crawledLinks = Sets.newHashSet();
		// Parse the link to get the link string and save them into the set. 11/23/2014, Bing Li 
		for (Element link : links)
		{
			// Parse the link to get the link string. 11/23/2014, Bing Li
			linkHref = link.attr(CrawlConfig.HREF);
			// Save the link string into the set. 11/23/2014, Bing Li
			crawledLinks.add(new CrawledLink(Tools.getHash(linkHref), linkHref, link.text(), hubURL.getKey()));
		}
		// Send the crawled links from the URL to the coordinator. 11/23/2014, Bing Li
		CrawlEventer.NOTIFY().sendCrawledLinks(crawledLinks);
		// Return the crawled links. 
		return crawledLinks;
	}
}
