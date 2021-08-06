package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 07/22/2019, Bing Li
public class VendorSignOutRequest extends ClusterRequest
{
	private static final long serialVersionUID = 4648153234477949829L;

	private String vendorKey;

	public VendorSignOutRequest(String vendorKey)
	{
		super(vendorKey, CommerceApplicationID.VENDOR_SIGN_OUT_REQUEST);
		this.vendorKey = vendorKey;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
}
