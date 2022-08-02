package org.greatfree.framework.container.multicast.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
public class StopRootNotification extends ClusterNotification
{
	private static final long serialVersionUID = 3686468168307626689L;

	public StopRootNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, MultiAppID.STOP_ROOT_NOTIFICATION);
	}

}
