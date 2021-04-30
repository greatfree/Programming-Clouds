package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/17/2020, Bing Li
public class DataLakeNodeRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = 3518628787977221703L;
	
	private boolean isSucceeded;

	public DataLakeNodeRegistryResponse(boolean isSucceeded, String collaboratorKey)
	{
		super(SCAppID.DATA_LAKE_NODE_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
