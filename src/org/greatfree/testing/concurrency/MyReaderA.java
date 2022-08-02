package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.Reader;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
class MyReaderA extends Reader<Query, Answer>
{

	@Override
	public Answer read(Query request)
	{
		try
		{
			return new Answer(SyncMethods.doSomethingA(request.getQuery()), request.getCollaboratorKey());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
