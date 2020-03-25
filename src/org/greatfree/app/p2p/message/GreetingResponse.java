package org.greatfree.app.p2p.message;

import org.greatfree.message.ServerMessage;

// Created: 08/19/2018, Bing Li
public class GreetingResponse extends ServerMessage
{
	private static final long serialVersionUID = -6184692505539129418L;
	
	private String appreciation;

	public GreetingResponse(String appreciation)
	{
		super(P2PMessageType.GREETING_RESPONSE);
		this.appreciation = appreciation;
	}

	public String getAppreciation()
	{
		return this.appreciation;
	}
}
