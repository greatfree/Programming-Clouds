package org.greatfree.testing.stress.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 11/07/2021, Bing Li
public class StopRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = -278981376217886711L;

	public StopRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, StressAppID.STOP_ROOT_NOTIFICATION);
	}

}
