package org.greatfree.framework.cluster.scalable.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 09/06/2020, Bing Li
public class StopTaskRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = -5984086022463158690L;

	public StopTaskRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ScalableApplicationID.STOP_TASK_ROOT_NOTIFICATION);
	}

}
