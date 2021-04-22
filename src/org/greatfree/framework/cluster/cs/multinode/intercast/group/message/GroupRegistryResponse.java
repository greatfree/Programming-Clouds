package org.greatfree.framework.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 04/02/2019, Bing Li
public class GroupRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = -2103782291787588582L;
	
	private boolean isSucceeded;

	public GroupRegistryResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(GroupChatApplicationID.GROUP_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
