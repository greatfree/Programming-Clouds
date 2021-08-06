package org.greatfree.app.search.container.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 01/14/2019, Bing Li
public class ShutdownChildrenAdminNotification extends ClusterNotification
{
	private static final long serialVersionUID = 1395100346252727863L;

	public ShutdownChildrenAdminNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, SearchApplicationID.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION);
	}

}
