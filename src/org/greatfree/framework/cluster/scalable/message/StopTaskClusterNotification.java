package org.greatfree.framework.cluster.scalable.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/06/2020, Bing Li
public class StopTaskClusterNotification extends Notification
{
	private static final long serialVersionUID = -4193180604103762257L;

	public StopTaskClusterNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ScalableApplicationID.STOP_TASK_CLUSTER_NOTIFICATION);
	}

}
