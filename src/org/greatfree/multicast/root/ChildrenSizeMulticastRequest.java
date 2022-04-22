package org.greatfree.multicast.root;

import java.util.Set;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/16/2018, Bing Li
public final class ChildrenSizeMulticastRequest
{
	private MulticastRequest request;
	private Set<String> childrenKeys;
	private int childrenSize;
	
	public ChildrenSizeMulticastRequest(MulticastRequest request, Set<String> childrenKeys, int childrenSize)
	{
		this.request = request;
		this.childrenKeys = childrenKeys;
		this.childrenSize = childrenSize;
	}
	
	public MulticastRequest getRequest()
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
