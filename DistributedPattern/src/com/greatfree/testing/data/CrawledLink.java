package com.greatfree.testing.data;

import java.io.Serializable;

/*
 * The class that contains the crawling results, the link and the text of the link. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawledLink implements Serializable
{
	private static final long serialVersionUID = -833353988970909339L;

	// The key of the link, which is created by the method, Tools.getAHash(). 11/23/2014, Bing Li
	private String key;
	// The link crawled from the hub URL. 11/23/2014, Bing Li
	private String link;
	// The text associated with the crawled link, such as the title of the link. 11/23/2014, Bing Li
	private String text;
	// The key of the hub URL being crawled. 11/23/2014, Bing Li
	private String hubURLKey;
	
	/*
	 * Initialize. 11/23/2014, Bing Li
	 */
	public CrawledLink(String key, String link, String text, String hubURLKey)
	{
		this.key = key;
		this.link = link;
		this.text = text;
		this.hubURLKey = hubURLKey;
	}

	/*
	 * Expose the key. 11/23/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Expose the link. 11/23/2014, Bing Li
	 */
	public String getLink()
	{
		return this.link;
	}

	/*
	 * Expose the text. 11/23/2014, Bing Li
	 */
	public String getText()
	{
		return this.text;
	}

	/*
	 * Expose the key of the hub URL being crawled. 11/23/2014, Bing Li
	 */
	public String getHubURLKey()
	{
		return this.hubURLKey;
	}
}
