package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.Request;

// Created: 07/14/2019, Bing Li
public class CartRequest extends Request
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
