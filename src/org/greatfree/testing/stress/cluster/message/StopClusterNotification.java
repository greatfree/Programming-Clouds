package org.greatfree.testing.stress.cluster.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 11/07/2021, Bing Li
public class StopClusterNotification extends ClusterNotification
{
	private static final long serialVersionUID = 831081944079263159L;

	public StopClusterNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, StressAppID.STOP_CLUSTER_NOTIFICATION);
	}

}
