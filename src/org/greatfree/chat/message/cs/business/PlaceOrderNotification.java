package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/07/2017, Bing Li
public class PlaceOrderNotification extends ServerMessage
{
	private static final long serialVersionUID = -7761179351848464402L;
	
	private String vendorKey;
	private String customerKey;

	public PlaceOrderNotification(String vendorKey, String customerKey)
	{
		super(BusinessMessageType.PLACE_ORDER_NOTIFICATION);
		this.vendorKey = vendorKey;
		this.customerKey = customerKey;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public String getCustomerKey()
	{
		return this.customerKey;
	}
}
