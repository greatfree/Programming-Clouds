package org.greatfree.dip.container.p2p.message;

import org.greatfree.message.container.Request;

// Created: 09/21/2019, Bing Li
public class AllRegisteredIPRequest extends Request
{
	private static final long serialVersionUID = -1717528174388208835L;

	public AllRegisteredIPRequest()
	{
		super(P2PChatApplicationID.ALL_REGISTERED_IPS_REQUEST);
	}

}