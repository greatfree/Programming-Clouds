package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/17/2020, Bing Li
public class SmartContractNodeRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = -3861194125986516139L;
	
	private boolean isSucceeded;

	public SmartContractNodeRegistryResponse(boolean isSucceeded, String collaboratorKey)
	{
		super(SCAppID.SMART_CONTRACT_NODE_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
