package org.greatfree.framework.cluster.original.cs.twonode.message;

import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 12/17/2018, Bing Li
public class StopServerOnClusterNotification extends Notification
{
	private static final long serialVersionUID = 3359279147811320702L;

	public StopServerOnClusterNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ClusterApplicationID.STOP_SERVER_ON_CLUSTER);
	}

}
