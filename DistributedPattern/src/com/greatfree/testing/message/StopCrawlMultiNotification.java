package com.greatfree.testing.message;

import java.util.HashMap;

import com.greatfree.multicast.ServerMulticastMessage;

/*
 * This notification is multicast to all of the crawlers to stop the crawling. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlMultiNotification extends ServerMulticastMessage
{
	private static final long serialVersionUID = -1784254133690452874L;

	/*
	 * Initialize. 11/27/2014, Bing Li
	 * 
	 * The key is used for binding multiple threads for synchronization management when all of them need to process the notification.
	 */
	public StopCrawlMultiNotification(String key)
	{
		super(MessageType.STOP_CRAWL_MULTI_NOTIFICATION, key);
	}

	/*
	 * Initialize. 11/27/2014, Bing Li
	 * 
	 * The key is used for binding multiple threads for synchronization management when all of them need to process the notification.
	 * 
	 * The collection, childrenServers, keeps all of the children keys and IPs. Through it, the node that receives the notification can send the notification to those nodes, the children.
	 * 
	 */
	public StopCrawlMultiNotification(String key, HashMap<String, String> childrenMap)
	{
		super(MessageType.STOP_CRAWL_MULTI_NOTIFICATION, key, childrenMap);
	}
}
