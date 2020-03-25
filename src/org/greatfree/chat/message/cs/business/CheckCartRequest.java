package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/11/2017, Bing Li
public class CheckCartRequest extends ServerMessage
{
	private static final long serialVersionUID = 6886257488676783986L;
	
	private String customerKey;
	private String vendorKey;

	public CheckCartRequest(String customerKey, String vendorKey)
	{
		super(BusinessMessageType.CHECK_CART_REQUEST);
		this.customerKey = customerKey;
		this.vendorKey = vendorKey;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}
}
