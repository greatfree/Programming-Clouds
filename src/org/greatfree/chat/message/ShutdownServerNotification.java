package org.greatfree.chat.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

/*
 * The notification aims to notify the chatting server to shutdown. 01/20/2016, Bing Li
 */

// Created: 04/17/2017, Bing Li
public class ShutdownServerNotification extends ServerMessage
{
	private static final long serialVersionUID = -745069317908956732L;

	public ShutdownServerNotification()
	{
		super(SystemMessageType.SHUTDOWN_SERVER_NOTIFICATION);
	}

}
