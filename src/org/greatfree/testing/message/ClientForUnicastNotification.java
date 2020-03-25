package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * The notification contains the data of a message. It is sent to a server such that the data on the server can be unicast. 02/06/2016, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForUnicastNotification extends ServerMessage
{
	private static final long serialVersionUID = -8674147296847898798L;
	
	private String message;

	public ClientForUnicastNotification(String message)
	{
		super(MessageType.CLIENT_FOR_UNICAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
