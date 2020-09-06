package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.Request;

// Created: 07/14/2019, Bing Li
public class CustomerSignInRequest extends Request
{
	private static final long serialVersionUID = 6914966117859578656L;
	
	private String customerKey;
	private String customerName;
	private String description;

	public CustomerSignInRequest(String customerKey, String customerName, String description)
	{
		super(customerKey, CommerceApplicationID.CUSTOMER_SIGN_IN_REQUEST);
		this.customerKey = customerKey;
		this.customerName = customerName;
		this.description = description;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
	
	public String getCustomerName()
	{
		return this.customerName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
