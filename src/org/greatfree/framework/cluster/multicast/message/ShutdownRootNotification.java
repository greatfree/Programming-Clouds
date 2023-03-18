package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class ShutdownRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = 8205597159425147598L;

	public ShutdownRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, ClusterAppID.SHUTDOWN_ROOT_NOTIFICATION);
	}

}
