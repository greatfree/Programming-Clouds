package org.greatfree.dip.container.p2p.message;

import org.greatfree.message.container.Request;

// Created: 10/03/2019, Bing Li
public class SelfRequest extends Request
{
	private static final long serialVersionUID = 2185945521681515591L;
	
	private String request;

	public SelfRequest(String request)
	{
		super(P2PChatApplicationID.SELF_REQUEST);
		this.request = request;
	}

	public String getRequest()
	{
		return this.request;
	}
}
