package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2019, Bing Li
public class CustomerSignInResponse extends MulticastResponse
{
	private static final long serialVersionUID = 6962716370903277717L;

	private boolean isSucceeded;

	public CustomerSignInResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.CUSTOMER_SIGN_IN_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
