package org.greatfree.multicast.rp.root;

import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/20/2018, Bing Li
class NearestKeyMulticastRequest
{
	private RPMulticastRequest request;
	private String dataKey;
	
	public NearestKeyMulticastRequest(RPMulticastRequest request, String dataKey)
	{
		this.request = request;
		this.dataKey = dataKey;
	}

	public RPMulticastRequest getRequest()
	{
		return this.request;
	}
	
	public String getDataKey()
	{
		return this.dataKey;
	}

}
