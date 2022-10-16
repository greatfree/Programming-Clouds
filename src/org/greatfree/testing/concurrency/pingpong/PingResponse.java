package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.Response;

/**
 * 
 * @author libing
 * 
 * 08/08/2022
 *
 */
class PingResponse extends Response
{
	private PingRequest ping;

	public PingResponse(PingRequest ping, String collaboratorKey)
	{
		super(collaboratorKey);
		this.ping = ping;
	}

	public PingRequest getPing()
	{
		return this.ping;
	}
}
