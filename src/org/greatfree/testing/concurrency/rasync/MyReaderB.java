package org.greatfree.testing.concurrency.rasync;

import org.greatfree.concurrency.Reader;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
class MyReaderB extends Reader<Query, Answer>
{

	@Override
	public Answer read(Query request)
	{
		try
		{
			return new Answer(SyncReadMethods.doSomethingB(request.getQuery()), request.getCollaboratorKey());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
