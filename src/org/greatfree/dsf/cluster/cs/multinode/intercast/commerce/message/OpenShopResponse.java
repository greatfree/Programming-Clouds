package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2019, Bing Li
public class OpenShopResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1210171133879157823L;

	private boolean isSucceeded;

	public OpenShopResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.OPEN_SHOP_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
