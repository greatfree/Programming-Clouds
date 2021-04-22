package org.greatfree.framework.multicast.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldAnycastResponse extends ServerMessage
{
	private static final long serialVersionUID = -2104959810710192983L;
	
	private List<HelloWorldAnycastResponse> responses;

	public ClientHelloWorldAnycastResponse(List<HelloWorldAnycastResponse> responses)
	{
		super(MulticastDIPMessageType.CLIENT_HELLO_WORLD_ANYCAST_RESPONSE);
		this.responses = responses;
	}

	public List<HelloWorldAnycastResponse> getResponses()
	{
		return this.responses;
	}
}
