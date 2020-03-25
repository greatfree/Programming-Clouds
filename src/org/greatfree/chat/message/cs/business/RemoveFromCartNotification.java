package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/07/2017, Bing Li
public class RemoveFromCartNotification extends ServerMessage
{
	private static final long serialVersionUID = -5139872452540305538L;
	
	private String customerKey;
	private String vendorKey;
	private String merchandiseKey;
	private int count;

	public RemoveFromCartNotification(String customerKey, String vendorKey, String merchandiseKey, int count)
	{
		super(BusinessMessageType.REMOVE_FROM_CART_NOTIFICATION);
		this.customerKey = customerKey;
		this.vendorKey = vendorKey;
		this.merchandiseKey = merchandiseKey;
		this.count = count;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}

	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
	
	public int getCount()
	{
		return this.count;
	}
}
