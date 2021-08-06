package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.ClusterRequest;

/*
 * The previous new features are interesting. But for the sign-in request, they might not be used. I need to consider for other messages. 07/22/2019, Bing Li
 * 
 * Although the message is a request, the intercasting message from the source child might be an inter-broadcasting notification. I have NOT done that before. Let me try it. 07/22/2019, Bing Li
 * 
 * When the source child receives the request, it needs to set the large number of destinations and the new messages to the children as a multicasting notification. 07/22/2019, Bing Li
 * 
 */

// Created: 07/22/2019, Bing Li
// public class VendorSignInRequest extends IntercastRequest
public class VendorSignInRequest extends ClusterRequest
{
	private static final long serialVersionUID = 9027919166310115788L;

	private String vendorKey;

	public VendorSignInRequest(String vendorKey)
	{
		super(vendorKey, CommerceApplicationID.VENDOR_SIGN_IN_REQUEST);
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
}
