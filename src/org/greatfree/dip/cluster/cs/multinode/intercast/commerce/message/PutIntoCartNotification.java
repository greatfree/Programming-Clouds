package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 07/14/2019, Bing Li
public class PutIntoCartNotification extends IntercastNotification
{
	private static final long serialVersionUID = 8345086027812856988L;

	private String customerKey;
	private String vendorKey;
	private Merchandise mc;
	private int count;
	
	public PutIntoCartNotification(String customerKey, String vendorKey, Merchandise mc, int count)
	{
		super(customerKey, vendorKey, CommerceApplicationID.PUT_INTO_CART_NOTIFICATION);
		this.customerKey = customerKey;
		this.vendorKey = vendorKey;
		this.mc = mc;
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
	
	public Merchandise getMerchandise()
	{
		return this.mc;
	}
	
	public int getCount()
	{
		return this.count;
	}
}
