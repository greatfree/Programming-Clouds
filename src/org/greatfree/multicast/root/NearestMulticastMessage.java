package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 09/10/2018, Bing Li
public class NearestMulticastMessage
{
	private String key;
	private MulticastMessage notification;
	
	public NearestMulticastMessage(String key, MulticastMessage notification)
	{
		this.key = key;
		this.notification = notification;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public MulticastMessage getMessage()
	{
		return this.notification;
	}
}
