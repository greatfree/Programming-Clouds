package org.greatfree.framework.cluster.scalable.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/05/2020, Bing Li
public class StopPoolClusterNotification extends Notification
{
	private static final long serialVersionUID = -736752172715665418L;

	public StopPoolClusterNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ScalableApplicationID.STOP_POOL_CLUSTER_NOTIFICATION);
	}

}
