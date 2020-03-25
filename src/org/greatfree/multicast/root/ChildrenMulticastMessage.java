package org.greatfree.multicast.root;

import java.util.Set;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 09/10/2018, Bing Li
public class ChildrenMulticastMessage
{
	private MulticastMessage notification;
	private Set<String> childrenKeys;

	public ChildrenMulticastMessage(MulticastMessage notification, Set<String> childrenKeys)
	{
		this.notification = notification;
		this.childrenKeys = childrenKeys;
	}
	
	public MulticastMessage getMessage()
	{
		return this.notification;
	}
	
	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}
}
