package org.greatfree.dsf.cluster.scalable.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/06/2020, Bing Li
public class StopTaskRootNotification extends Notification
{
	private static final long serialVersionUID = -5984086022463158690L;

	public StopTaskRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ScalableApplicationID.STOP_TASK_ROOT_NOTIFICATION);
	}

}
