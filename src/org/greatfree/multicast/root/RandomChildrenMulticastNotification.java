package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/11/2020, Bing Li
class RandomChildrenMulticastNotification
{
	private MulticastNotification notification;
	private int childrenSize;
	
	public RandomChildrenMulticastNotification(int childrenSize, MulticastNotification notification)
	{
		this.childrenSize = childrenSize;
		this.notification = notification;
	}
	
	public int getChildrenSize()
	{
		return this.childrenSize;
	}

	public MulticastNotification getNotification()
	{
		return this.notification;
	}
}
