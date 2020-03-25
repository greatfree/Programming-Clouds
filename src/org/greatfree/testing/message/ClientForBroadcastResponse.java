package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * This is a response to be sent to the coordinator. It must raise a broadcast request within the cluster under the control of the coordinator. After all of the responses are collected by the coordinator, the current response is formed and responded to the client. 11/29/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForBroadcastResponse extends ServerMessage
{
	private static final long serialVersionUID = 2049734782201472683L;
	
	private String message;

	public ClientForBroadcastResponse(String message)
	{
		super(MessageType.CLIENT_FOR_BROADCAST_RESPONSE);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
