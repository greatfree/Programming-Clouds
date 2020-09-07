package org.greatfree.dsf.cluster.replication.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/07/2020, Bing Li
public class StopReplicationRootNotification extends Notification
{
	private static final long serialVersionUID = 6424622595232998064L;

	public StopReplicationRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ReplicationApplicationID.STOP_REPLICATION_ROOT_NOTIFICATION);
	}

}
