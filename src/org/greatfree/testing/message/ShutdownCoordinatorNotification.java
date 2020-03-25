package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * The notification is raised by the administrator and then sent to the coordinator. In the sample, the coordinator is a standalone machine such that it can be terminated when receiving the notification. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class ShutdownCoordinatorNotification extends ServerMessage
{
	private static final long serialVersionUID = -6480028802376313087L;

	/*
	 * Initialize. No any arguments are needed. 11/27/2014, Bing Li
	 */
	public ShutdownCoordinatorNotification()
	{
		super(MessageType.SHUTDOWN_COORDINATOR_NOTIFICATION);
	}

}
