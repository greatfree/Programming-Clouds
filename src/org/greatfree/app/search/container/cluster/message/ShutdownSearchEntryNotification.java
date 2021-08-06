package org.greatfree.app.search.container.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 01/14/2019, Bing Li
public class ShutdownSearchEntryNotification extends ClusterNotification
{
	private static final long serialVersionUID = -5830504026780457410L;

	public ShutdownSearchEntryNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, SearchApplicationID.SHUTDOWN_SERVER_NOTIFICATION);
	}

}
