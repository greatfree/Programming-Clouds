package org.greatfree.multicast.rp.root;

import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/20/2018, Bing Li
class SizeMulticastRequest
{
	private RPMulticastRequest request;
	private int childrenSize;

	public SizeMulticastRequest(RPMulticastRequest request, int childrenSize)
	{
		this.request = request;
		this.childrenSize = childrenSize;
	}

	public RPMulticastRequest getRequest()
	{
		return this.request;
	}
	
	public int getChildrenSize()
	{
		return this.childrenSize;
	}
}
