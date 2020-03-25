package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/15/2017, Bing Li
public class CheckPendingOrderRequest extends ServerMessage
{
	private static final long serialVersionUID = 1732904953845605149L;
	
	private String vendorKey;

	public CheckPendingOrderRequest(String vendorKey)
	{
		super(BusinessMessageType.CHECK_PENDING_ORDER_REQUEST);
		this.vendorKey = vendorKey;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
}
