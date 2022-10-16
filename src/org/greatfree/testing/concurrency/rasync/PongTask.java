package org.greatfree.testing.concurrency.rasync;

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
//	private Query ping;
	
	private ReaderPool<Query, Answer> pongerPool;
//	private Query pong;
	
	private final String collaboratorKey;
	
	private int maxCount;
	private int currentCount;
	
//	public PongTask(Reader<Query, Answer> pinger, Query ping, ReaderPool<Query, Answer> pongerPool, Query pong, int maxCount)
	public PongTask(Reader<Query, Answer> pinger, ReaderPool<Query, Answer> pongerPool, String collaboratorKey, int maxCount)
	{
		this.pinger = pinger;
//		this.ping = ping;
		this.pongerPool = pongerPool;
//		this.pong = pong;
		this.collaboratorKey = collaboratorKey;
		this.maxCount = maxCount;
		this.currentCount = 0;
	}

	@Override
	public void run()
	{
		Answer answer;
		do
		{
			System.out.println("\nRound " + ++this.currentCount);
			System.out.println("PongTask: waiting for ping ...");
//			answer = (Answer)this.pinger.waitForResponse(this.ping.getCollaboratorKey(), 6000);
			answer = (Answer)this.pinger.waitForResponse(this.collaboratorKey, 6000);
			if (answer != null)
			{
				System.out.println("PongTask: " + answer.getAnswer() + " received ...");
				if (this.currentCount < this.maxCount)
				{
//					this.pong.setQuery("pong");
					Query pong = new Query("pong", this.collaboratorKey);
					this.pongerPool.read(pong);
					System.out.println("PongTask: " + pong.getQuery() + " sent ...");
				}
				else
				{
//					this.pong.setQuery("exit");
//					Query pong = new Query("exit", this.collaboratorKey);
					this.pongerPool.read(new Query("exit", this.collaboratorKey));
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
