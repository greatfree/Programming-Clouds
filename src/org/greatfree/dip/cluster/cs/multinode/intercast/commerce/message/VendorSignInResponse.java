package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/22/2019, Bing Li
public class VendorSignInResponse extends MulticastResponse
{
	private static final long serialVersionUID = -8953166162360662001L;
	
	private boolean isSucceeded;

	public VendorSignInResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.VENDOR_SIGN_IN_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
