package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 05/12/2022
 *
 */
public class ShutdownChildrenNotification extends ClusterNotification
{
	private static final long serialVersionUID = 1855813194991878136L;

	public ShutdownChildrenNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, ClusterAppID.SHUTDOWN_CHILDREN_NOTIFICATION);
	}

}
