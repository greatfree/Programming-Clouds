package org.greatfree.framework.cluster.original.cs.twonode.message;

import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 02/24/2019, Bing Li
public class StopChatClusterThroughRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = 3274644293959183728L;

	public StopChatClusterThroughRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION);
	}

}
