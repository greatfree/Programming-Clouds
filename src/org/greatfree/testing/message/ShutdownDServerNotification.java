package org.greatfree.testing.message;

import org.greatfree.message.container.Notification;

// Created: 03/30/2020, Bing Li
public class ShutdownDServerNotification extends Notification
{
	private static final long serialVersionUID = -8770090997427267924L;

	public ShutdownDServerNotification()
	{
		super(ApplicationID.SHUTDOWN_D_SERVER_NOTIFICATION);
	}

}
