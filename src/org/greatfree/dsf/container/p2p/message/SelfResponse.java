package org.greatfree.dsf.container.p2p.message;

import org.greatfree.message.ServerMessage;

// Created: 10/03/2019, Bing Li
public class SelfResponse extends ServerMessage
{
	private static final long serialVersionUID = -2129395703805078614L;
	
	private String response;

	public SelfResponse(String response)
	{
		super(P2PChatApplicationID.SELF_RESPONSE);
		this.response = response;
	}

	public String getResponse()
	{
		return this.response;
	}
}
