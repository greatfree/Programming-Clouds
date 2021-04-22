package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/22/2019, Bing Li
public class VendorSignOutResponse extends MulticastResponse
{
	private static final long serialVersionUID = 4129702385317759107L;
	
	private boolean isSucceeded;

	public VendorSignOutResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.VENDOR_SIGN_OUT_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
