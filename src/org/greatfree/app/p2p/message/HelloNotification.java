package org.greatfree.app.p2p.message;

import org.greatfree.message.ServerMessage;

// Created: 08/19/2018, Bing Li
public class HelloNotification extends ServerMessage
{
	private static final long serialVersionUID = 4856979358337840965L;
	
	private String helloWorld;

	public HelloNotification(String helloWorld)
	{
		super(P2PMessageType.HELLO_NOTIFICATION);
		this.helloWorld = helloWorld;
	}

	public String getHelloWorld()
	{
		return this.helloWorld;
	}
}
