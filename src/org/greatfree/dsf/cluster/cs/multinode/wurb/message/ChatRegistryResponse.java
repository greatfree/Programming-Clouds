package org.greatfree.dsf.cluster.cs.multinode.wurb.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 01/28/2019, Bing Li
public class ChatRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = 395980173365390021L;

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
