package org.greatfree.multicast.rp.root;

import java.util.Set;

import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/20/2018, Bing Li
class ChildrenSizeMulticastRequest
{
	private RPMulticastRequest request;
	private Set<String> childrenKeys;
	private int childrenSize;
	
	public ChildrenSizeMulticastRequest(RPMulticastRequest request, Set<String> childrenKeys, int childrenSize)
	{
		this.request = request;
		this.childrenKeys = childrenKeys;
		this.childrenSize = childrenSize;
	}
	
	public RPMulticastRequest getRequest()
	{
		return this.request;
	}
	
	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}
	
	public int getChildrenSize()
	{
		return this.childrenSize;
	}
}
