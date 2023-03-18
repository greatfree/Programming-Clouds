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
public class HelloInterAnycastNotification extends IntercastNotification
{
	private static final long serialVersionUID = -5346937727503303500L;

	private String message;

	public HelloInterAnycastNotification(String message, Set<String> dstKeys)
	{
		super(MulticastMessageType.INTER_ANYCAST_NOTIFICATION, Tools.generateUniqueKey(), dstKeys, ClusterAppID.HELLO_INTER_ANYCAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
