package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 07/14/2019, Bing Li
public class CustomerRegistryRequest extends ClusterRequest
{
	private static final long serialVersionUID = -4906410323965398349L;
	
	private String customerKey;
	private String customerName;
	private String description;

	public CustomerRegistryRequest(String customerKey, String customerName, String description)
	{
		super(customerKey, CommerceApplicationID.CUSTOMER_REGISTRY_REQUEST);
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
