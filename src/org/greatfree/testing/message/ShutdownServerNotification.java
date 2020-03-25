package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * The notification aims to notify the regular Server to shutdown. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
public class ShutdownServerNotification extends ServerMessage
{
	private static final long serialVersionUID = -327085617944172044L;

	/*
	 * Initializing ... 01/20/2016, Bing Li
	 */
	public ShutdownServerNotification()
	{
		super(MessageType.SHUTDOWN_REGULAR_SERVER_NOTIFICATION);
	}
}
