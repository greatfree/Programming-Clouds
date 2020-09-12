package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/10/2018, Bing Li
public class NearestMulticastNotification
{
	private String key;
	private MulticastNotification notification;
	
	public NearestMulticastNotification(String key, MulticastNotification notification)
	{
		this.key = key;
		this.notification = notification;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public MulticastNotification getNotification()
	{
		return this.notification;
	}
}
