package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message;

import java.util.List;

import org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child.MerchandisePost;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 07/14/2019, Bing Li
public class PollMerchandiseResponse extends MulticastResponse
{
	private static final long serialVersionUID = 9137446688689019475L;
	
	public List<MerchandisePost> merchandises;

	public PollMerchandiseResponse(List<MerchandisePost> merchandises, String collaboratorKey)
	{
		super(CommerceApplicationID.POLL_MERCHANDISE_RESPONSE, collaboratorKey);
		this.merchandises = merchandises;
	}

	public List<MerchandisePost> getMerchandises()
	{
		return this.merchandises;
	}
}
