package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class HelloAnycastRequest extends ClusterRequest
{
	private static final long serialVersionUID = 4953270166926466522L;
	
	private String message;

	public HelloAnycastRequest(String message)
	{
		super(MulticastMessageType.ANYCAST_REQUEST, ClusterAppID.HELLO_ANYCAST_REQUEST);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
