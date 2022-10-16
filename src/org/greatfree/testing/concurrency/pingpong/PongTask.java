package org.greatfree.testing.concurrency.pingpong;

import org.greatfree.concurrency.Reader;
import org.greatfree.concurrency.ReaderPool;
import org.greatfree.concurrency.RunnerTask;

/**
 * 
 * @author libing
 * 
 * 08/07/2022
 *
 */
final class PongTask extends RunnerTask
{
	private Reader<PingRequest, PingResponse> pinger;
	private ReaderPool<PongRequest, PongResponse> pongerPool;
	private final String pingKey;
	private final String pongKey;
	
	private int maxCount;
	private int currentCount;
	
	public PongTask(Reader<PingRequest, PingResponse> pinger, ReaderPool<PongRequest, PongResponse> pongerPool, String pingKey, String pongKey, int maxCount)
	{
		this.pinger = pinger;
		this.pongerPool = pongerPool;
		this.pingKey = pingKey;
		this.pongKey = pongKey;
		this.maxCount = maxCount;
		this.currentCount = 0;
	}

	@Override
	public void run()
	{
		PingResponse answer;
		do
		{
			System.out.println("\nRound " + ++this.currentCount);
			System.out.println("PongTask: waiting for ping ...");
			answer = (PingResponse)this.pinger.waitForResponse(this.pingKey, ReaderPoolConfig.WAIT_RESPONSE_TIME);
			if (answer != null)
			{
				System.out.println("PongTask: " + answer.getPing().getQuery() + " received ...");
				if (this.currentCount < this.maxCount)
				{
					try
					{
						PongRequest pong = new PongRequest(PingPongMethods.pong("pong"), this.pongKey);
						this.pongerPool.read(pong);
						System.out.println("PongTask: " + pong.getQuery() + " sent ...");
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					this.pongerPool.read(new PongRequest("exit", this.pongKey));
					break;
				}
			}
		}
		while (true);
	}

	@Override
	public int getWorkload()
	{
		return 0;
	}

	@Override
	public void dispose() throws InterruptedException
	{
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
	}
}
