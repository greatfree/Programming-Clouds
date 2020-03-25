package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * This is a request to be sent to the coordinator. It must raise an anycast request within the cluster under the control of the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForAnycastRequest extends ServerMessage
{
	private static final long serialVersionUID = -4812383536209509265L;
	
	private String message;

	public ClientForAnycastRequest(String message)
	{
		super(MessageType.CLIENT_FOR_ANYCAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
