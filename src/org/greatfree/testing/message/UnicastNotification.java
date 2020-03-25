package org.greatfree.testing.message;

import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;

/*
 * This notification is unicast to one particular DN of a cluster. 11/25/2016, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastNotification extends ServerMulticastMessage
{
	private static final long serialVersionUID = 2345569539252454416L;
	
	private String message;

	public UnicastNotification(String key, String message)
	{
		super(MessageType.UNICAST_NOTIFICATION, key);
		this.message = message;
	}

	public UnicastNotification(String key, HashMap<String, String> childrenServers, String message)
	{
		super(MessageType.UNICAST_NOTIFICATION, key, childrenServers);
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
