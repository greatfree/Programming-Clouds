package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class HelloUnicastResponse extends MulticastResponse
{
	private static final long serialVersionUID = 3621664498702775273L;

	private String message;

	public HelloUnicastResponse(String message, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_UNICAST_RESPONSE, collaboratorKey);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
