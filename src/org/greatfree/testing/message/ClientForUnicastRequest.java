package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * This is a request to be sent to the coordinator. It must raise a unicast request within the cluster under the control of the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForUnicastRequest extends ServerMessage
{
	private static final long serialVersionUID = 218363968994452562L;
	
	private String message;

	public ClientForUnicastRequest(String message)
	{
		super(MessageType.CLIENT_FOR_UNICAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
