package org.greatfree.multicast.root;

import java.util.Set;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/15/2018, Bing Li
final public class ChildrenMulticastRequest
{
	private MulticastRequest request;
	private Set<String> childrenKeys;
	
	public ChildrenMulticastRequest(MulticastRequest request, Set<String> childrenKeys)
	{
		this.request = request;
		this.childrenKeys = childrenKeys;
	}
	
	public MulticastRequest getRequest()
	{
		return this.request;
	}
	
	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}

}
