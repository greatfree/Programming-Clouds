package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message;

import java.util.List;

import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.server.child.MerchandisePost;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2019, Bing Li
public class PollVendorResponse extends MulticastResponse
{
	private static final long serialVersionUID = 5284262434957582746L;
	
	public List<MerchandisePost> merchandises;

	public PollVendorResponse(List<MerchandisePost> merchandises, String collaboratorKey)
	{
		super(CommerceApplicationID.POLL_VENDOR_RESPONSE, collaboratorKey);
		this.merchandises = merchandises;
	}

	public List<MerchandisePost> getMerchandises()
	{
		return this.merchandises;
	}
}
