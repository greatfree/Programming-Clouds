package org.greatfree.chat.message.cs.business;

import org.greatfree.message.ServerMessage;

// Created: 12/06/2017, Bing Li
public class PutIntoCartNotification extends ServerMessage
{
	private static final long serialVersionUID = -2664051494066072118L;
	
	private String customerKey;
	private String vendorKey;
	private String merchandiseKey;
	private int count;

	public PutIntoCartNotification(String customerKey, String vendorKey, String merchandiseKey, int count)
	{
		super(BusinessMessageType.PUT_INTO_CART_NOTIFICATION);
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
