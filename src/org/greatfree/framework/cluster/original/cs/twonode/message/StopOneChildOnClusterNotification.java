package org.greatfree.framework.cluster.original.cs.twonode.message;

import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.util.Tools;

// Created: 12/17/2018, Bing Li
public class StopOneChildOnClusterNotification extends ClusterNotification
{
	private static final long serialVersionUID = -1778523077035828002L;

	public StopOneChildOnClusterNotification()
	{
//		super(Tools.generateUniqueKey(), MulticastMessageType.UNICAST_NOTIFICATION, ClusterApplicationID.STOP_ONE_CHILD_ON_CLUSTER_NOTIFICATION);
		super(Tools.generateUniqueKey(), ClusterApplicationID.STOP_ONE_CHILD_ON_CLUSTER_NOTIFICATION);
	}

}
