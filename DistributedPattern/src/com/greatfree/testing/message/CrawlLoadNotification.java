package com.greatfree.testing.message;

import java.util.HashMap;
import java.util.Map;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.testing.data.URLValue;

/*
 * This is a notification sent to a crawler to assign the URLs to it for crawling. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlLoadNotification extends ServerMessage
{
	private static final long serialVersionUID = 2075087040783551384L;

	// The crawler key to be sent to. 11/25/2014, Bing Li
	private String dcKey;
	// The URLs to be crawled by the crawler. 11/25/2014, Bing Li
	private Map<String, URLValue> urls;
	// Since the load might be high, the load might be sent to the crawler in a piece of notifications. The flag denotes which one is the first. 11/25/2014, Bing Li
	private boolean isFirst;
	// Since the load might be high, the load might be sent to the crawler in a piece of notifications. The flag denotes which one is the last one. 11/25/2014, Bing Li
	private boolean isDone;

	/*
	 * Initialize the notification. 11/25/2014, Bing Li
	 */
	public CrawlLoadNotification(String dcKey, boolean isFirst)
	{
		super(MessageType.CRAWL_LOAD_NOTIFICATION);
		this.dcKey = dcKey;
		this.urls = new HashMap<String, URLValue>();
		this.isFirst = isFirst;
		this.isDone = false;
	}

	/*
	 * Expose the crawler key. 11/25/2014, Bing Li
	 */
	public String getDCKey()
	{
		return this.dcKey;
	}

	/*
	 * Expose the URLs to be assigned. 11/25/2014, Bing Li
	 */
	public Map<String, URLValue> getURLs()
	{
		return this.urls;
	}

	/*
	 * Set the URLs to be assigned. 11/25/2014, Bing Li
	 */
	public void setURLs(Map<String, URLValue> urls)
	{
		this.urls = urls;
	}

	/*
	 * Expose the flag of whether the notification is the first one to assign the load to the crawler. 11/25/2014, Bing Li
	 */
	public boolean isFirst()
	{
		return this.isFirst;
	}

	/*
	 * Expose the flag of whether the notification is the last one to assign the load to the crawler. 11/25/2014, Bing Li
	 */
	public boolean isDone()
	{
		return this.isDone;
	}

	/*
	 * Set the done flag. 11/25/2014, Bing Li
	 */
	public void setDone()
	{
		this.isDone = true;
	}
}
