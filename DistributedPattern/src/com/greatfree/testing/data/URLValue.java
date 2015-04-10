package com.greatfree.testing.data;

import java.io.Serializable;

/*
 * The class contains the URL to be crawled. Meanwhile, the class can be transmitted between the coordinator and the crawler. Thus, it must implement the interface, Serializable. 11/28/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class URLValue implements Serializable
{
	private static final long serialVersionUID = -8117874901342689273L;

	// The key of the URL. 11/28/2014, Bing Li
	private String key;
	// The URL to be crawled. 11/28/2014, Bing Li
	private String url;
	// The updating period of the URL. According to it, the crawler can determine the moment to crawl it. 11/28/2014, Bing Li
	private long updatingPeriod;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public URLValue()
	{
		this.key = Constants.NO_URL_KEY;
		this.url = Constants.NO_URL;
		this.updatingPeriod = Constants.NO_UPDATING_PERIOD;
	}
	
	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public URLValue(String key, String url, long updatingPeriod)
	{
		this.key = key;
		this.url = url;
		this.updatingPeriod = updatingPeriod;
	}
	
	/*
	 * Expose the key of the URL. 11/28/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}
	
	/*
	 * Expose the URL. 11/28/2014, Bing Li
	 */
	public String getURL()
	{
		return this.url;
	}
	
	/*
	 * Expose the updating period. 11/28/2014, Bing Li
	 */
	public long getUpdatingPeriod()
	{
		return this.updatingPeriod;
	}
}
