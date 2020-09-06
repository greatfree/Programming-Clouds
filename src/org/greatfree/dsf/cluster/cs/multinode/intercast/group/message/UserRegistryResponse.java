package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 04/02/2019, Bing Li
public class UserRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = -571109711721725125L;
	
	private boolean isSucceeded;

	public UserRegistryResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(GroupChatApplicationID.USER_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
