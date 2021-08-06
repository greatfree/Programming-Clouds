package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 07/22/2019, Bing Li
public class VendorRegistryRequest extends ClusterRequest
{
	private static final long serialVersionUID = -6960285762083522280L;
	
	private String vendorKey;
	private String vendorName;
	private String description;

	public VendorRegistryRequest(String vendorKey, String vendorName, String description)
	{
		super(vendorKey, CommerceApplicationID.VENDOR_REGISTRY_REQUEST);
		this.vendorKey = vendorKey;
		this.vendorName = vendorName;
		this.description = description;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public String getVendorName()
	{
		return this.vendorName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
