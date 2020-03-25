package org.greatfree.demo.cluster.mncs.message;

import org.greatfree.app.container.cs.multinode.business.message.Merchandise;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 02/17/2019, Bing Li
public class MerchandiseResponse extends MulticastResponse
{
	private static final long serialVersionUID = 5321469995148002363L;

	private Merchandise m;

	public MerchandiseResponse(Merchandise m, String collaboratorKey)
	{
		super(BusinessApplicationID.MERCHANDISE_RESPONSE, collaboratorKey);
		this.m = m;
	}

	public Merchandise getMerchandise()
	{
		return this.m;
	}
}
