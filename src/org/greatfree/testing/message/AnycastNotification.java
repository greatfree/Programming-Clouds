package org.greatfree.testing.message;

import java.util.HashMap;

import org.greatfree.message.ServerMulticastMessage;

/*
 * This notification is anycast to one particular DN of a cluster. 11/25/2016, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotification extends ServerMulticastMessage
{
	private static final long serialVersionUID = -1854905149995628352L;
	
	private String message;

	public AnycastNotification(String key, String message)
	{
		super(MessageType.ANYCAST_NOTIFICATION, key);
		this.message = message;
	}
	
	public AnycastNotification(String key, HashMap<String, String> childrenServers, String message)
	{
		super(MessageType.ANYCAST_NOTIFICATION, key, childrenServers);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
