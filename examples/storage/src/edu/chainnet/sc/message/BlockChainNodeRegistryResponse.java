package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/17/2020, Bing Li
public class BlockChainNodeRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = 5312943656770890621L;
	
	private boolean isSucceeded;

	public BlockChainNodeRegistryResponse(boolean isSucceeded, String collaboratorKey)
	{
		super(SCAppID.BLOCK_CHAIN_NODE_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
