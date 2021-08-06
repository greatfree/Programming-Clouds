package org.greatfree.framework.cluster.scalable.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 09/05/2020, Bing Li
public class StopPoolRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = 3730437248678768421L;

	public StopPoolRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ScalableApplicationID.STOP_POOL_ROOT_NOTIFICATION);
	}

}
