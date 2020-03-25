package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/20/2017, Bing Li
public class CheckVendorTransactionRequest extends ServerMessage
{
	private static final long serialVersionUID = 8350324549132101713L;
	
	private String vendorKey;

	public CheckVendorTransactionRequest(String vendorKey)
	{
		super(BusinessMessageType.CHECK_VENDOR_TRANSACTION_REQUEST);
		this.vendorKey = vendorKey;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
}
