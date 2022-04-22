package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/15/2018, Bing Li
public class SizeMulticastRequest
{
	private MulticastRequest request;
	private int childrenSize;
	
	public SizeMulticastRequest(MulticastRequest request, int childrenSize)
	{
		this.request = request;
		this.childrenSize = childrenSize;
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
