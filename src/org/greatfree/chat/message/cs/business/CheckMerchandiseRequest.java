package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/05/2017, Bing Li
public class CheckMerchandiseRequest extends ServerMessage
{
	private static final long serialVersionUID = -2125397852797776486L;
	
	private String vendorKey;
	
	public CheckMerchandiseRequest(String vendorKey)
	{
		super(BusinessMessageType.CHECK_MERCHANDISE_REQUEST);
		this.vendorKey = vendorKey;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}
}
