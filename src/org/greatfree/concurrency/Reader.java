package org.greatfree.concurrency;

import org.greatfree.concurrency.reactive.RendezvousPoint;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
public abstract class Reader<Query extends Request, Answer extends Response>
{
	public Response waitForResponse(String collaboratorKey, long timeout)
	{
		return RendezvousPoint.REDUCE().waitForResponse(collaboratorKey, timeout);
	}
	
	public abstract Answer read(Query request);
}
