package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.Request;

// Created: 07/14/2019, Bing Li
public class CustomerSignOutRequest extends Request
{
	private static final long serialVersionUID = 1363243990636761934L;
	
	private String customerKey;

	public CustomerSignOutRequest(String customerKey)
	{
		super(customerKey, CommerceApplicationID.CUSTOMER_SIGN_OUT_REQUEST);
		this.customerKey = customerKey;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
}
