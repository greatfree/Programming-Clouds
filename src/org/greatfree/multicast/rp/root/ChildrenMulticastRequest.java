package org.greatfree.multicast.rp.root;

import java.util.Set;

import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/20/2018, Bing Li
class ChildrenMulticastRequest
{
	private RPMulticastRequest request;
	private Set<String> childrenKeys;
	
	public ChildrenMulticastRequest(RPMulticastRequest request, Set<String> childrenKeys)
	{
		this.request = request;
		this.childrenKeys = childrenKeys;
	}
	
	public RPMulticastRequest getRequest()
	{
		return this.request;
	}
	
	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}
}
