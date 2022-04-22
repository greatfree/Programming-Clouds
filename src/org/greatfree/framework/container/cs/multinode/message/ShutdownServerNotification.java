package org.greatfree.framework.container.cs.multinode.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Notification;

// Created: 01/07/2019, Bing Li
public class ShutdownServerNotification extends Notification
{
	private static final long serialVersionUID = -3425084077023724337L;

	public ShutdownServerNotification()
	{
		super(SystemMessageType.SHUTDOWN_SERVER_NOTIFICATION);
	}

}
