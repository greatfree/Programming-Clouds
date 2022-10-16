package org.greatfree.testing.concurrency.rasync;

import org.greatfree.concurrency.Reader;

/**
 * 
 * @author libing
 * 
 * 08/02/2022
 *
 */
final class PingReader extends Reader<Query, Answer>
{

	@Override
	public Answer read(Query request)
	{
		try
		{
			return new Answer(PingPongMethods.ping(request.getQuery()), request.getCollaboratorKey());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
