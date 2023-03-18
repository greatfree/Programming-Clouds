package org.greatfree.framework.cs.disabled.broker.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 03/17/2023
 *
 */
public class ShutdownChildrenNotification extends ClusterNotification
{
	private static final long serialVersionUID = 1669619731737182541L;

	public ShutdownChildrenNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, DisabledAppID.SHUTDOWN_CHILDREN_NOTIFICATION);
	}
}
