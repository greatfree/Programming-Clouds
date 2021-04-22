package org.greatfree.framework.multicast.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldBroadcastResponse extends ServerMessage
{
	private static final long serialVersionUID = 37869317537251934L;
	
	private List<HelloWorldBroadcastResponse> responses;

	public ClientHelloWorldBroadcastResponse(List<HelloWorldBroadcastResponse> responses)
	{
		super(MulticastDIPMessageType.CLIENT_HELLO_WORLD_BROADCAST_RESPONSE);
		this.responses = responses;
	}

	public List<HelloWorldBroadcastResponse> getResponses()
	{
		return this.responses;
	}
}
