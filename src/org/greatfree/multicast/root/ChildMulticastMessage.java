package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 09/10/2018, Bing Li
public class ChildMulticastMessage
{
	private MulticastMessage notification;
	private String clientKey;
	
	public ChildMulticastMessage(MulticastMessage notification, String clientKey)
	{
		this.notification = notification;
		this.clientKey = clientKey;
	}

	public MulticastMessage getMessage()
	{
		return this.notification;
	}
	
	public String getChildKey()
	{
		return this.clientKey;
	}
}
