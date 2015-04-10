package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

/*
 * The notification is raised by the administrator and then sent to the coordinator. Through the coordinator, the relevant notification is multicast to all of the memory nodes to stop working. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class ShutdownMemoryServerNotification extends ServerMessage
{
	private static final long serialVersionUID = 4490404955904893382L;

	/*
	 * Initialize. No any arguments are needed. 11/27/2014, Bing Li
	 */
	public ShutdownMemoryServerNotification()
	{
		super(MessageType.SHUTDOWN_MEMORY_SERVER_NOTIFICATION);
	}
}
