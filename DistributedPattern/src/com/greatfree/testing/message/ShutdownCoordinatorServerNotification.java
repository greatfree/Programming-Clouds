package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * The notification is raised by the administrator and then sent to the coordinator. In the sample, the coordinator is a standalone machine such that it can be terminated when receiving the notification. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class ShutdownCoordinatorServerNotification extends ServerMessage
{
	private static final long serialVersionUID = 9220677303206367246L;

	/*
	 * Initialize. No any arguments are needed. 11/27/2014, Bing Li
	 */
	public ShutdownCoordinatorServerNotification()
	{
		super(MessageType.SHUTDOWN_COORDINATOR_SERVER_NOTIFICATION);
	}
}
