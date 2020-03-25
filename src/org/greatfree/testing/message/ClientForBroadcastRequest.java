package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * This is a request to be sent to the coordinator. It must raise a broadcast request within the cluster under the control of the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForBroadcastRequest extends ServerMessage
{
	private static final long serialVersionUID = 2102522041658076230L;
	
	private String message;

	public ClientForBroadcastRequest(String message)
	{
		super(MessageType.CLIENT_FOR_BROADCAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
