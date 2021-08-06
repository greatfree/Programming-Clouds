package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 07/14/2019, Bing Li
public class CartRequest extends ClusterRequest
{
	private static final long serialVersionUID = 5998973397835426699L;
	
	private String customerKey;

	public CartRequest(String customerKey, int applicationID)
	{
		super(customerKey, CommerceApplicationID.CART_REQUEST);
		this.customerKey = customerKey;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
}
