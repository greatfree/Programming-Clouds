package org.greatfree.concurrency;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
public final class AsyncReaderPool<Query extends Request, Answer extends Response> extends Thread implements IdleCheckable
{
//	private Map<String, Runner<Inquirier>>

	@Override
	public void checkIdle() throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		// TODO Auto-generated method stub
		
	}

}
