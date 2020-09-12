package org.greatfree.multicast.root;

import java.util.Set;

import org.greatfree.message.multicast.MulticastNotification;

// Created: 09/10/2018, Bing Li
public class ChildrenMulticastNotification
{
	private MulticastNotification notification;
	private Set<String> childrenKeys;

	public ChildrenMulticastNotification(MulticastNotification notification, Set<String> childrenKeys)
	{
		this.notification = notification;
		this.childrenKeys = childrenKeys;
	}
	
	public MulticastNotification getNotification()
	{
		return this.notification;
	}
	
	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}
}
