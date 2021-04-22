package org.greatfree.framework.multicast.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldShutdownChildrenBroadcastNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = 8852935272874213085L;

	public OldShutdownChildrenBroadcastNotification()
	{
		super(MulticastDIPMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION);
	}

	public OldShutdownChildrenBroadcastNotification(HashMap<String, IPAddress> childrenServers)
	{
		super(MulticastDIPMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION, childrenServers);
	}

}
