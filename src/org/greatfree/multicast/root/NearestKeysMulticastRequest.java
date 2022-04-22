package org.greatfree.multicast.root;

import java.util.Set;

import org.greatfree.message.multicast.MulticastRequest;

// Created: 09/16/2018, Bing Li
public final class NearestKeysMulticastRequest
{
	private MulticastRequest request;
	private Set<String> dataKeys;
	
	public NearestKeysMulticastRequest(MulticastRequest request, Set<String> dataKeys)
	{
		this.request = request;
		this.dataKeys = dataKeys;
	}
	
	public MulticastRequest getRequest()
	{
		return this.request;
	}

	public Set<String> getDataKeys()
	{
		return this.dataKeys;
	}
}
