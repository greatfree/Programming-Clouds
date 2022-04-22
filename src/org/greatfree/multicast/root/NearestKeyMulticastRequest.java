package org.greatfree.multicast.root;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/16/2018, Bing Li
public final class NearestKeyMulticastRequest
{
	private MulticastRequest request;
	private String dataKey;
	
	public NearestKeyMulticastRequest(MulticastRequest request, String dataKey)
	{
		this.request = request;
		this.dataKey = dataKey;
	}

	public MulticastRequest getRequest()
	{
		return this.request;
	}
	
	public String getDataKey()
	{
		return this.dataKey;
	}
}
