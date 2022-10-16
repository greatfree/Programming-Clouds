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
final class PingTask extends RunnerTask
{
	private Reader<PongRequest, PongResponse> ponger;
	private ReaderPool<PingRequest, PingResponse> pingerPool;
	private final String pingKey;
	private final String pongKey;
	
	public PingTask(Reader<PongRequest, PongResponse> ponger, ReaderPool<PingRequest, PingResponse> pingerPool, String pingKey, String pongKey)
	{
		this.ponger = ponger;
		this.pingerPool = pingerPool;
		this.pingKey = pingKey;
		this.pongKey = pongKey;
	}

	@Override
	public void run()
	{
		PongResponse answer;
		do
		{
			System.out.println("PingTask: waiting for pong ...");
			answer = (PongResponse)this.ponger.waitForResponse(this.pongKey, ReaderPoolConfig.WAIT_RESPONSE_TIME);
			if (answer != null)
			{
				System.out.println("PingTask: " + answer.getPong().getQuery() + " received");
				if (answer.getPong().getQuery().indexOf("exit") < 0)
				{
					try
					{
						PingRequest ping = new PingRequest(PingPongMethods.ping("ping"), this.pingKey);
						this.pingerPool.read(ping);
						System.out.println("PingTask: " + ping.getQuery() + " sent");
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				else
				{
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

