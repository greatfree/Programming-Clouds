package org.greatfree.testing.message;

import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;

/*
 * This notification is broadcast to all of the DN servers. 11/26/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotification extends ServerMulticastMessage
{
	private static final long serialVersionUID = 1028339731887808447L;
	
	private String message;

	public BroadcastNotification(String key, String message)
	{
		super(MessageType.BROADCAST_NOTIFICATION, key);
		this.message = message;
	}

	public BroadcastNotification(String key, HashMap<String, String> childrenServers, String message)
	{
		super(MessageType.BROADCAST_NOTIFICATION, key, childrenServers);
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
