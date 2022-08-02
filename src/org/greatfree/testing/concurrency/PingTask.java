package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.Reader;
import org.greatfree.concurrency.ReaderPool;
import org.greatfree.concurrency.RunnerTask;

/**
 * 
 * @author libing
 * 
 * 08/02/2022
 *
 */
final class PingTask extends RunnerTask
{
	private Reader<Query, Answer> ponger;
	private Query pong;
	
	private ReaderPool<Query, Answer> pingerPool;
	private Query ping;
	
	public PingTask(Reader<Query, Answer> ponger, Query pong, ReaderPool<Query, Answer> pingerPool, Query ping)
	{
		this.ponger = ponger;
		this.pong = pong;
		this.pingerPool = pingerPool;
		this.ping = ping;
	}

	@Override
	public void run()
	{
		Answer pong;
		do
		{
			System.out.println("PingTask: waiting for pong ...");
			pong = (Answer)this.ponger.waitForResponse(this.pong.getCollaboratorKey(), 6000);
			if (pong != null)
			{
				System.out.println("PingTask: " + pong.getAnswer() + " received");
				if (pong.getAnswer().indexOf("exit") < 0)
				{
					this.ping.setQuery("ping");
					this.pingerPool.read(this.ping);
					System.out.println("PingTask: " + this.ping.getQuery() + " sent");
				}
				else
				{
					break;
				}
			}
			else
			{
				System.out.println("PingTask: pong is null");
				break;
			}
		}
		while (true);
		
		
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
