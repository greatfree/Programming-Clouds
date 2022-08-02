package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.Reader;
import org.greatfree.concurrency.ReaderPool;
import org.greatfree.concurrency.RunnerTask;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
final class IntermediateTask extends RunnerTask
{
	private Reader<Query, Answer> srcReader;
	private Query srcQuery;
	
	private ReaderPool<Query, Answer> dstReaderPool;
	private Query dstQuery;
	
	public IntermediateTask(Reader<Query, Answer> srcReader, Query srcQuery, ReaderPool<Query, Answer> dstReaderPool, Query dstQuery)
	{
		this.srcReader = srcReader;
		this.srcQuery = srcQuery;
		this.dstReaderPool = dstReaderPool;
		this.dstQuery = dstQuery;
	}

	@Override
	public void run()
	{
		System.out.println("IntermediateTask: waiting for the answer ...");
		Answer answer = (Answer)this.srcReader.waitForResponse(this.srcQuery.getCollaboratorKey(), 6000);
		if (answer != null)
		{
			System.out.println("IntermediateTask: response is " + answer.getAnswer());
			this.dstQuery.setQuery(answer.getAnswer());
			this.dstReaderPool.read(this.dstQuery);
		}
		else
		{
			System.out.println("response is null");
		}
	}

	@Override
	public int getWorkload()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

}
