package org.greatfree.testing.message;

import org.greatfree.message.ServerMessage;

/*
 * The notification is raised by the administrator and then sent to the coordinator. Through the coordinator, the relevant notification is multicast to all of the crawlers to stop the crawling process. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class ShutdownDNNotification extends ServerMessage
{
	private static final long serialVersionUID = 7399327996050735868L;

	/*
	 * Initialize. No any arguments are needed. 11/27/2014, Bing Li
	 */
	public ShutdownDNNotification()
	{
		super(MessageType.SHUTDOWN_DN_NOTIFICATION);
	}

}
