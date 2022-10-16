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
final class PingTask extends RunnerTask
{
	private Reader<Query, Answer> ponger;
//	private Query pong;
	
	private ReaderPool<Query, Answer> pingerPool;
//	private Query ping;
	private final String collaboratorKey;
	
//	public PingTask(Reader<Query, Answer> ponger, Query pong, ReaderPool<Query, Answer> pingerPool, Query ping)
	public PingTask(Reader<Query, Answer> ponger, ReaderPool<Query, Answer> pingerPool, String collaboratorKey)
	{
		this.ponger = ponger;
//		this.pong = pong;
		this.pingerPool = pingerPool;
//		this.ping = ping;
		this.collaboratorKey = collaboratorKey;
	}

	@Override
	public void run()
	{
		Answer answer;
		do
		{
			System.out.println("PingTask: waiting for pong ...");
//			answer = (Answer)this.ponger.waitForResponse(this.pong.getCollaboratorKey(), 6000);
			answer = (Answer)this.ponger.waitForResponse(this.collaboratorKey, 6000);
			if (answer != null)
			{
				System.out.println("PingTask: " + answer.getAnswer() + " received");
				if (answer.getAnswer().indexOf("exit") < 0)
				{
//					this.ping.setQuery("ping");
					Query ping = new Query("ping", this.collaboratorKey);
					this.pingerPool.read(ping);
					System.out.println("PingTask: " + ping.getQuery() + " sent");
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
