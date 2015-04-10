package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * The notification is raised by the administrator and then sent to the coordinator. Through the coordinator, the relevant notification is multicast to all of the crawlers to stop the crawling process. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class ShutdownCrawlServerNotification extends ServerMessage
{
	private static final long serialVersionUID = 3492103470869232426L;

	/*
	 * Initialize. No any arguments are needed. 11/27/2014, Bing Li
	 */
	public ShutdownCrawlServerNotification()
	{
		super(MessageType.SHUTDOWN_CRAWL_SERVER_NOTIFICATION);
	}
}
