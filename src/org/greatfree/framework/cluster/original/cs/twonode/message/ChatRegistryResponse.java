package org.greatfree.framework.cluster.original.cs.twonode.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/24/2018, Bing Li
public class ChatRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = 3643358469241310657L;

	// The result of one account's registry. 04/15/2017, Bing Li
	private boolean isSucceeded;

	public ChatRegistryResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(ChatApplicationID.CHAT_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
