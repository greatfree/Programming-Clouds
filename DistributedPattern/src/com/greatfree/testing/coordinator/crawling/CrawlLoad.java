package com.greatfree.testing.coordinator.crawling;

import java.util.HashMap;

import com.greatfree.testing.data.URLValue;

/*
 * This is a workload to crawl, assigned to a particular crawler represented as the DC key. DC stands for the term, Distributed Component. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlLoad
{
	// The key of the crawler. 11/25/2014, Bing Li
	private String dcKey;
	// The URLs that the crawler to be crawled. 11/25/2014, Bing Li
	private HashMap<String, URLValue> urls;

	/*
	 * Initialize. 11/25/2014, Bing Li
	 */
	public CrawlLoad(String dcKey, HashMap<String, URLValue> urls)
	{
		this.dcKey = dcKey;
		this.urls = urls;
	}

	/*
	 * Expose the crawler key. 11/25/2014, Bing Li
	 */
	public String getDCKey()
	{
		return this.dcKey;
	}

	/*
	 * Expose the URLs to be crawled by the crawler. 11/25/2014, Bing Li
	 */
	public HashMap<String, URLValue> getURLs()
	{
		return this.urls;
	}
}
