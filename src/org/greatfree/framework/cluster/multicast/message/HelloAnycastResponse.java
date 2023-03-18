package org.greatfree.framework.cluster.multicast.message;

import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 04/27/2022
 *
 */
public class HelloAnycastResponse extends MulticastResponse
{
	private static final long serialVersionUID = -7385159571349250036L;
	
	private String message;

	public HelloAnycastResponse(String message, String collaboratorKey)
	{
		super(ClusterAppID.HELLO_ANYCAST_RESPONSE, collaboratorKey);
		this.message = message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
