package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.IntercastRequest;

// Created: 07/14/2019, Bing Li
public class PlaceOrderRequest extends IntercastRequest
{
	private static final long serialVersionUID = -4437683929122925103L;

	private String customerKey;
	private String vendorKey;
	private String merchandiseKey;
	private int count;
	private float payment;

	public PlaceOrderRequest(String customerKey, String vendorKey, String merchandiseKey, int count, float payment)
	{
		super(customerKey, vendorKey, CommerceApplicationID.PLACE_ORDER_REQUEST);
		this.customerKey = customerKey;
		this.vendorKey = vendorKey;
		this.merchandiseKey = merchandiseKey;
		this.count = count;
		this.payment = payment;
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
	
	public float getPayment()
	{
		return this.payment;
	}
}
