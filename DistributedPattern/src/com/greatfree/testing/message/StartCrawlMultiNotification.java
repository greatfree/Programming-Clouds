package com.greatfree.testing.message;

import java.util.HashMap;

import com.greatfree.multicast.ServerMulticastMessage;

/*
 * This notification is multicast to all of the crawlers to start the crawling. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlMultiNotification extends ServerMulticastMessage
{
	private static final long serialVersionUID = -2832937472197546783L;

	/*
	 * Initialize. 11/26/2014, Bing Li
	 * 
	 * The key is used for binding multiple threads for synchronization management when all of them need to process the notification.
	 */
	public StartCrawlMultiNotification(String key)
	{
		super(MessageType.START_CRAWL_MULTI_NOTIFICATION, key);
	}

	/*
	 * Initialize. 11/26/2014, Bing Li
	 * 
	 * The key is used for binding multiple threads for synchronization management when all of them need to process the notification.
	 * 
	 * The collection, childrenServers, keeps all of the children keys and IPs. Through it, the node that receives the notification can send the notification to those nodes, the children.
	 * 
	 */
	public StartCrawlMultiNotification(String key, HashMap<String, String> childrenServers)
	{
		super(MessageType.START_CRAWL_MULTI_NOTIFICATION, key, childrenServers);
	}
}
