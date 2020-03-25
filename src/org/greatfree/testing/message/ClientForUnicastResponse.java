package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * This is a response to be sent to the coordinator. It must raise a unicast request within the cluster under the control of the coordinator. After the response from the particular clustering node is received by the coordinator, the current response is formed and responded to the client. 11/29/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForUnicastResponse extends ServerMessage
{
	private static final long serialVersionUID = 5233107032428628086L;
	
	private String message;

	public ClientForUnicastResponse(String message)
	{
		super(MessageType.CLIENT_FOR_UNICAST_RESPONSE);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
