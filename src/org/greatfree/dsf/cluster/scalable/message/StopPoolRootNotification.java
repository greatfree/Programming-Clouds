package org.greatfree.dsf.cluster.scalable.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 09/05/2020, Bing Li
public class StopPoolRootNotification extends Notification
{
	private static final long serialVersionUID = 3730437248678768421L;

	public StopPoolRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ScalableApplicationID.STOP_POOL_ROOT_NOTIFICATION);
	}

}
