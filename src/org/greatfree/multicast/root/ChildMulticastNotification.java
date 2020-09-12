package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/10/2018, Bing Li
public class ChildMulticastNotification
{
	private MulticastNotification notification;
	private String clientKey;
	
	public ChildMulticastNotification(MulticastNotification notification, String clientKey)
	{
		this.notification = notification;
		this.clientKey = clientKey;
	}

	public MulticastNotification getNotification()
	{
		return this.notification;
	}
	
	public String getChildKey()
	{
		return this.clientKey;
	}
}
