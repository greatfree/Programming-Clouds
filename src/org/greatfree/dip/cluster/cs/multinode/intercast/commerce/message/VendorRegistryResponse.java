package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/22/2019, Bing Li
public class VendorRegistryResponse extends MulticastResponse
{
	private static final long serialVersionUID = -4194203653391288655L;
	
	private boolean isSucceeded;

	public VendorRegistryResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.VENDOR_REGISTRY_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
