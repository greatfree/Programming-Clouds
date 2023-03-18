package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.container.ClusterNotification;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class HelloUnicastNotification extends ClusterNotification
{
	private static final long serialVersionUID = -7863506859153535284L;
	
	private String message;

	public HelloUnicastNotification(String message)
	{
		super(ClusterAppID.HELLO_UNICAST_NOTIFICATION);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
