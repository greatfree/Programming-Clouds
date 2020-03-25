package org.greatfree.dip.multicast.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldUnicastResponse extends ServerMessage
{
	private static final long serialVersionUID = -6560698272712783508L;
	
	private List<HelloWorldUnicastResponse> responses;

	public ClientHelloWorldUnicastResponse(List<HelloWorldUnicastResponse> responses)
	{
		super(MulticastDIPMessageType.CLIENT_HELLO_WORLD_UNOCAST_RESPONSE);
		this.responses = responses;
	}

	public List<HelloWorldUnicastResponse> getResponses()
	{
		return this.responses;
	}
}
