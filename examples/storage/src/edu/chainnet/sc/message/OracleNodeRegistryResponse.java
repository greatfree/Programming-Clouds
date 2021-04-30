package edu.chainnet.sc.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 10/17/2020, Bing Li
public class OracleNodeRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = -1120207878873552455L;
	
	private boolean isSucceeded;

	public OracleNodeRegistryResponse(boolean isSucceeded, String collaboratorKey)
	{
		super(SCAppID.ORACLE_NODE_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
