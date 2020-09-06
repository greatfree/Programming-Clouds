package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 07/14/2019, Bing Li
public class PostMerchandiseNotification extends IntercastNotification
{
	private static final long serialVersionUID = -2083906340127876468L;
	
	private String vendorKey;
	private String storeKey;
	private Merchandise mc;

	public PostMerchandiseNotification(String vendorKey, String storeKey, Merchandise mc)
	{
		super(vendorKey, storeKey, CommerceApplicationID.POST_MERCHANDISE_NOTIFICATION);
		this.vendorKey = vendorKey;
		this.storeKey = storeKey;
		this.mc = mc;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public String getStoreKey()
	{
		return this.storeKey;
	}
	
	public Merchandise getMerchandise()
	{
		return this.mc;
	}
}
