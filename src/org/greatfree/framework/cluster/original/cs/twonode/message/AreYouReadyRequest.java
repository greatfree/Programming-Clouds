package org.greatfree.framework.cluster.original.cs.twonode.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/15/2019, Bing Li
public class AreYouReadyRequest extends ClusterRequest
{
	private static final long serialVersionUID = -6180419987966630736L;
	
	private String resourceKey;

	public AreYouReadyRequest(String rscKey)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, ChatApplicationID.ARE_YOU_READY_REQUEST);
		this.resourceKey = rscKey;
	}

	public String getResourceKey()
	{
		return this.resourceKey;
	}
}
