package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.IntercastRequest;

// Created: 07/14/2019, Bing Li
public class OpenShopRequest extends IntercastRequest
{
	private static final long serialVersionUID = 3396851625289460318L;
	
	private String vendorKey;
	private String vendorName;
	private String vendorDescription;
	
	private String shopKey;
	private String shopName;
	private String shopDescription;

	public OpenShopRequest(String vendorKey, String shopKey, String shopName, String shopDescription, String vendorName, String vendorDescription)
	{
		super(vendorKey, shopKey, CommerceApplicationID.OPEN_SHOP_REQUEST);
		this.vendorKey = vendorKey;
		this.vendorName = vendorName;
		this.vendorDescription = vendorDescription;
		this.shopKey = shopKey;
		this.shopName = shopName;
		this.shopDescription = shopDescription;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public String getVendorName()
	{
		return this.vendorName;
	}
	
	public String getVendorDescription()
	{
		return this.vendorDescription;
	}
	
	public String getShopKey()
	{
		return this.shopKey;
	}
	
	public String getShopName()
	{
		return this.shopName;
	}
	
	public String getShopDescription()
	{
		return this.shopDescription;
	}

}
