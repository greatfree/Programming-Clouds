package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 06/19/2022
 *
 */
public class HelloInterUnicastNotification extends IntercastNotification
{
	private static final long serialVersionUID = -6555063573789074780L;
	
	private String message;

	public HelloInterUnicastNotification(String message)
	{
		super(Tools.generateUniqueKey(), Tools.generateUniqueKey(), ClusterAppID.HELLO_INTER_UNICAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
