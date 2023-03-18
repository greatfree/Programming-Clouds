package org.greatfree.framework.cs.disabled.message;

import org.greatfree.message.container.Notification;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
public class DSShutdownNotification extends Notification
{
	private static final long serialVersionUID = -6227803350071152883L;

	public DSShutdownNotification()
	{
		super(DisabledAppID.DS_SHUTDOWN_NOTIFICATION);
	}
}
