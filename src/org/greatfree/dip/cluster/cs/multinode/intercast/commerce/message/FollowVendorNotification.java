package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 07/14/2019, Bing Li
public class FollowVendorNotification extends IntercastNotification
{
	private static final long serialVersionUID = 9096020547571260948L;
	
	private String customerKey;
	private String vendorKey;

	public FollowVendorNotification(String customerKey, String vendorKey)
	{
		super(customerKey, vendorKey, CommerceApplicationID.FOLLOW_VENDOR_NOTIFICATION);
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
