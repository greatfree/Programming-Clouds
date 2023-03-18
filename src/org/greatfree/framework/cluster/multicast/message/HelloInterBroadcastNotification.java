package org.greatfree.framework.cluster.multicast.message;

import java.util.Set;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 06/20/2022
 *
 */
public class HelloInterBroadcastNotification extends IntercastNotification
{
	private static final long serialVersionUID = -2359767868011655839L;

	private String message;

	public HelloInterBroadcastNotification(String message, Set<String> dstKeys)
	{
		super(MulticastMessageType.INTER_BROADCAST_NOTIFICATION, Tools.generateUniqueKey(), dstKeys, ClusterAppID.HELLO_INTER_BROADCAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
