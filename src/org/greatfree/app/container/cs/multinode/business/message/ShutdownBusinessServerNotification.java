package org.greatfree.app.container.cs.multinode.business.message;

import org.greatfree.message.container.Notification;

// Created: 01/24/2019, Bing Li
public class ShutdownBusinessServerNotification extends Notification
{
	private static final long serialVersionUID = -6832564736433315883L;

	public ShutdownBusinessServerNotification()
	{
		super(ApplicationID.SHUTDOWN_BUSINESS_SERVER_NOTIFICATION);
	}

}

