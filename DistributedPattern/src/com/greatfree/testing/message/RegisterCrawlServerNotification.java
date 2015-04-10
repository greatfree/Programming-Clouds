package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * This is the notification for a crawler to register on the coordinator. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class RegisterCrawlServerNotification extends ServerMessage
{
	private static final long serialVersionUID = 4999830006599286378L;

	// The key of the crawler server. Here, DC stands for the term, distributed component. 11/23/2014, Bing Li
	private String dcKey;
	// The count of URLs the crawler server needs to crawl. 11/23/2014, Bing Li
	private long urlCount;

	/*
	 * Initialize. 11/23/2014, Bing Li
	 */
	public RegisterCrawlServerNotification(String dcKey, long urlCount)
	{
		super(MessageType.REGISTER_CRAWL_SERVER_NOTIFICATION);
		this.dcKey = dcKey;
		this.urlCount = urlCount;
	}

	/*
	 * Expose the key of the crawler. 11/23/2014, Bing Li
	 */
	public String getDCKey()
	{
		return this.dcKey;
	}

	/*
	 * Expose the URL count. 11/23/2014, Bing Li
	 */
	public long getURLCount()
	{
		return this.urlCount;
	}
}
