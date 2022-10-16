package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.Reader;

/**
 * 
 * @author libing
 * 
 * 08/07/2022
 *
 */
final class PingReader extends Reader<PingRequest, PingResponse>
{
	@Override
	public PingResponse read(PingRequest request)
	{
		return new PingResponse(request, request.getCollaboratorKey());
	}
}

