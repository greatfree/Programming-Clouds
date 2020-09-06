package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.Request;

// Created: 07/22/2019, Bing Li
public class VendorSignOutRequest extends Request
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
