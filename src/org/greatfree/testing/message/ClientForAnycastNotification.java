package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * The notification contains the data of a message. It is sent to a server such that the data on the server can be anycast. 02/06/2016, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForAnycastNotification extends ServerMessage
{
	private static final long serialVersionUID = 2228429130788174987L;
	
	private String message;

	public ClientForAnycastNotification(String message)
	{
		super(MessageType.CLIENT_FOR_ANYCAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
