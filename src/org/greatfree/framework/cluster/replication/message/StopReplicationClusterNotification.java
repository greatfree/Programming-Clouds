package org.greatfree.framework.cluster.replication.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/07/2020, Bing Li
public class StopReplicationClusterNotification extends Notification
{
	private static final long serialVersionUID = 3903389264494774004L;

	public StopReplicationClusterNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ReplicationApplicationID.STOP_REPLICATION_CLUSTER_NOTIFICATION);
	}

}
