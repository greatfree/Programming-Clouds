package com.greatfree.testing.memory;

import com.greatfree.util.FreeObject;

/*
 * This is the data that is saved in the distributed memory server. It must derive from FreeObject in order to fit the requirements of ResrouceCache. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class LinkRecord extends FreeObject
{
	private String key;
	private String link;
	private String text;
	private String hubURLKey;
	
	public LinkRecord(String key, String link, String text, String hubURLKey)
	{
		this.key = key;
		this.link = link;
		this.text = text;
		this.hubURLKey = hubURLKey;
	}

	/*
	 * Expose the key. 11/28/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Expose the link. 11/28/2014, Bing Li
	 */
	public String getLink()
	{
		return this.link;
	}

	/*
	 * Expose the text. 11/28/2014, Bing Li
	 */
	public String getText()
	{
		return this.text;
	}

	/*
	 * Expose the key of the hub URL being crawled. 11/28/2014, Bing Li
	 */
	public String getHubURLKey()
	{
		return this.hubURLKey;
	}
}
