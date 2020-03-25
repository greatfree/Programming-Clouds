package org.greatfree.dip.cluster.original.cs.twonode.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 01/15/2019, Bing Li
public class AreYouReadyResponse extends MulticastResponse
{
	private static final long serialVersionUID = 2227715523882526002L;
	
	private String resourceKey;
	private boolean isReady;

	public AreYouReadyResponse(String collaboratorKey, String rscKey, boolean isReady)
	{
		super(ChatApplicationID.ARE_YOU_READY_RESPONSE, collaboratorKey);
		this.resourceKey = rscKey;
		this.isReady = isReady;
	}
	
	public String getResourceKey()
	{
		return this.resourceKey;
	}

	public boolean isReady()
	{
		return this.isReady;
	}
}
