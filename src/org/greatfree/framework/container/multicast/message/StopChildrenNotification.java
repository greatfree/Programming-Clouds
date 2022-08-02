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
public class StopChildrenNotification extends ClusterNotification
{
	private static final long serialVersionUID = -8778848444302648066L;

	public StopChildrenNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, MultiAppID.STOP_CHILDREN_NOTIFICATION);
	}

}
