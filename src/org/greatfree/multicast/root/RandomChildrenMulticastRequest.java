package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/11/2020, Bing Li
public final class RandomChildrenMulticastRequest
{
	private MulticastRequest request;
	private int childrenSize;
	
	public RandomChildrenMulticastRequest(MulticastRequest request, int n)
	{
		this.request = request;
		this.childrenSize = n;
	}

	public MulticastRequest getRequest()
	{
		return this.request;
	}

	public int getChildrenSize()
	{
		return this.childrenSize;
	}
}
