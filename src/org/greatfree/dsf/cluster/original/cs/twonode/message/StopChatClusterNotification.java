package org.greatfree.dsf.cluster.original.cs.twonode.message;

import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 10/28/2018, Bing Li
public class StopChatClusterNotification extends Notification
{
	private static final long serialVersionUID = -3705513265854537925L;

	public StopChatClusterNotification()
	{
//		super(ClusterMessageType.BROADCAST_NOTIFICATION, ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION);
//		super(MulticastMessageType.LOCAL_NOTIFICATION, ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION);
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION);
	}
	
	/*
	public StopServerOnClusterNotification(HashMap<String, IPAddress> childrenServers)
	{
		super(ClusterMessageType.BROADCAST_NOTIFICATION, ClusterApplicationID.STOP_SERVER_ON_CLUSTER_NOTIFICATION, childrenServers);
	}
	*/
}
