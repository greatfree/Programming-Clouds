package org.greatfree.message.multicast.container;

import java.util.List;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 09/23/2018, Bing Li
public class Response extends ServerMessage
{
	private static final long serialVersionUID = -2701535132846878238L;
	
//	private int responseType;
	private List<MulticastResponse> responses;

//	public Response(List<MulticastResponse> responses)
	public Response(int type, List<MulticastResponse> responses)
	{
//		super(MulticastMessageType.RESPONSE);
		super(type);
//		this.responseType = responseType;
		this.responses = responses;
	}

	/*
	public int getResponseType()
	{
		return this.responseType;
	}
	*/
	
	public List<MulticastResponse> getResponses()
	{
		return this.responses;
	}
}
