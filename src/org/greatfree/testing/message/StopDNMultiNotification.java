package org.greatfree.testing.message;

import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;

/*
 * This notification is multicast to all of the DN to stop them. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNMultiNotification extends ServerMulticastMessage
{
	private static final long serialVersionUID = -7330792273011683872L;

	/*
	 * Initialize. 11/27/2014, Bing Li
	 * 
	 * The key is used for binding multiple threads for synchronization management when all of them need to process the notification.
	 */
	public StopDNMultiNotification(String key)
	{
		super(MessageType.STOP_DN_NOTIFICATION, key);
	}

	/*
	 * Initialize. 11/27/2014, Bing Li
	 * 
	 * The key is used for binding multiple threads for synchronization management when all of them need to process the notification.
	 * 
	 * The collection, childrenServers, keeps all of the children keys and IPs. Through it, the node that receives the notification can send the notification to those nodes, the children.
	 * 
	 */
	public StopDNMultiNotification(String key, HashMap<String, String> childrenMap)
	{
		super(MessageType.STOP_DN_NOTIFICATION, key, childrenMap);
	}
	
}
