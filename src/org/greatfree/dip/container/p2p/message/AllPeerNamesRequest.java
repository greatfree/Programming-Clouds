package org.greatfree.dip.container.p2p.message;

import org.greatfree.message.container.Request;

// Created: 09/26/2019, Bing Li
public class AllPeerNamesRequest extends Request
{
	private static final long serialVersionUID = 4362682221564006065L;

	public AllPeerNamesRequest()
	{
		super(P2PChatApplicationID.ALL_PEER_NAMES_REQUEST);
	}

}
