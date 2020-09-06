package org.greatfree.dsf.multicast.message;

import org.greatfree.message.ServerMessage;

/*
 * The notification works as a remote command from the administrator to the root of the cluster. Then, the root broadcasts it to 
 */

// Created: 05/19/2017, Bing Li
public class ShutdownChildrenAdminNotification extends ServerMessage
{
	private static final long serialVersionUID = -5803616520557522304L;

	public ShutdownChildrenAdminNotification()
	{
		super(MulticastDIPMessageType.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION);
	}

}
