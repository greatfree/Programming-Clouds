package org.greatfree.multicast.rp.root;

import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/20/2018, Bing Li
class ChildKeyMulticastRequest
{
	private RPMulticastRequest request;
	private String childKey;
	
	public ChildKeyMulticastRequest(RPMulticastRequest request, String childKey)
	{
		this.request = request;
		this.childKey = childKey;
	}

	public RPMulticastRequest getRequest()
	{
		return this.request;
	}
	
	public String getChildKey()
	{
		return this.childKey;
	}
}
