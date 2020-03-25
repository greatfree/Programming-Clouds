package org.greatfree.multicast.rp.root;

import java.util.Set;

import org.greatfree.message.multicast.RPMulticastRequest;

// Created: 10/20/2018, Bing Li
class NearestKeysMulticastRequest
{
	private RPMulticastRequest request;
	private Set<String> dataKeys;
	
	public NearestKeysMulticastRequest(RPMulticastRequest request, Set<String> dataKeys)
	{
		this.request = request;
		this.dataKeys = dataKeys;
	}
	
	public RPMulticastRequest getRequest()
	{
		return this.request;
	}

	public Set<String> getDataKeys()
	{
		return this.dataKeys;
	}

}
