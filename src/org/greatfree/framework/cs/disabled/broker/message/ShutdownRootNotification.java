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
public class ShutdownRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = -3301069230844323036L;

	public ShutdownRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, DisabledAppID.SHUTDOWN_ROOT_NOTIFICATION);
	}
}
