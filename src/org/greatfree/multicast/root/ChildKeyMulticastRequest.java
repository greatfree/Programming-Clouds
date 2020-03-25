package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/16/2018, Bing Li
class ChildKeyMulticastRequest
{
	private MulticastRequest request;
	private String childKey;
	
	public ChildKeyMulticastRequest(MulticastRequest request, String childKey)
	{
		this.request = request;
		this.childKey = childKey;
	}

	public MulticastRequest getRequest()
	{
		return this.request;
	}
	
	public String getChildKey()
	{
		return this.childKey;
	}
}
