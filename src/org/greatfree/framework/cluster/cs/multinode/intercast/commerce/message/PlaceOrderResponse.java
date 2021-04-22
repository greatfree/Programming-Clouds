package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2019, Bing Li
public class PlaceOrderResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1610162417353438720L;

	private boolean isSucceeded;

	public PlaceOrderResponse(String collaboratorKey, boolean isSucceeded)
	{
		super(CommerceApplicationID.PLACE_ORDER_RESPONSE, collaboratorKey);
		this.isSucceeded = isSucceeded;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
