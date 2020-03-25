package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2019, Bing Li
public class CustomerRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = 3299933087009352910L;
	
	private boolean isSucceeded;

	public CustomerRegistryResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.CUSTOMER_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
