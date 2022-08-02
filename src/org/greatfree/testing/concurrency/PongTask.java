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
final class PongTask extends RunnerTask
{
	private Reader<Query, Answer> pinger;
	private Query ping;
	
	private ReaderPool<Query, Answer> pongerPool;
	private Query pong;
	
	private int maxCount;
	private int currentCount;
	
	public PongTask(Reader<Query, Answer> pinger, Query ping, ReaderPool<Query, Answer> pongerPool, Query pong, int maxCount)
	{
		this.pinger = pinger;
		this.ping = ping;
		this.pongerPool = pongerPool;
		this.pong = pong;
		this.maxCount = maxCount;
		this.currentCount = 0;
	}

	@Override
	public void run()
	{
		Answer ping;
		do
		{
			System.out.println("\nRound " + ++this.currentCount);
			System.out.println("PongTask: waiting for ping ...");
			ping = (Answer)this.pinger.waitForResponse(this.ping.getCollaboratorKey(), 6000);
			if (ping != null)
			{
				System.out.println("PongTask: " + ping.getAnswer() + " received ...");
				if (this.currentCount < this.maxCount)
				{
					this.pong.setQuery("pong");
					this.pongerPool.read(this.pong);
					System.out.println("PongTask: " + this.pong.getQuery() + " sent ...");
				}
				else
				{
					this.pong.setQuery("exit");
					this.pongerPool.read(this.pong);
					break;
				}
			}
			else
			{
				System.out.println("PongTask: ping is null");
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
