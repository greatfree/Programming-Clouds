package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2019, Bing Li
public class CustomerSignOutResponse extends MulticastResponse
{
	private static final long serialVersionUID = -2845879358173648339L;

	private boolean isSucceeded;

	public CustomerSignOutResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.CUSTOMER_SIGN_OUT_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
