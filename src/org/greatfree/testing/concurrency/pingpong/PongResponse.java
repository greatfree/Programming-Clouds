package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.Response;

/**
 * 
 * @author libing
 * 
 * 08/08/2022
 *
 */
class PongResponse extends Response
{
	private PongRequest pong;

	public PongResponse(PongRequest pong, String collaboratorKey)
	{
		super(collaboratorKey);
		this.pong = pong;
	}

	public PongRequest getPong()
	{
		return this.pong;
	}
}
