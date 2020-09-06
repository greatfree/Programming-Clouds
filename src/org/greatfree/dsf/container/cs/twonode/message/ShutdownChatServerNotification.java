package org.greatfree.dsf.container.cs.twonode.message;

import org.greatfree.message.container.Notification;

// Created: 12/18/2018, Bing Li
public class ShutdownChatServerNotification extends Notification
{
	private static final long serialVersionUID = -4824008307504500328L;

	public ShutdownChatServerNotification()
	{
//		super(CSMessageType.NOTIFICATION, ApplicationID.SHUTDOWN_CHAT_SERVER_NOTIFICATION);
		super(ApplicationID.SHUTDOWN_CHAT_SERVER_NOTIFICATION);
	}

}
