package org.greatfree.app.p2p.message;

import org.greatfree.message.ServerMessage;

// Created: 08/19/2018, Bing Li
public class GreetingRequest extends ServerMessage
{
	private static final long serialVersionUID = -5000980306643082749L;
	
	private String greeting;

	public GreetingRequest(String greeting)
	{
		super(P2PMessageType.GREETING_REQUEST);
		this.greeting = greeting;
	}

	public String getGreeting()
	{
		return this.greeting;
	}
}
