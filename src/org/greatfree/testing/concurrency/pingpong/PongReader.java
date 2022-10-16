package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.Reader;

/**
 * 
 * @author libing
 * 
 * 08/07/2022
 *
 */
final class PongReader extends Reader<PongRequest, PongResponse>
{
	@Override
	public PongResponse read(PongRequest request)
	{
		return new PongResponse(request, request.getCollaboratorKey());
	}
}

