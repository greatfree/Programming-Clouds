package org.greatfree.concurrency;

import org.greatfree.concurrency.reactive.RequestObjectQueue;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
final class RequestThread<Query extends Request, Answer extends Response> extends RequestObjectQueue<Query, Response>
{
	private Reader<Query, Answer> reader;
	private long waitTime;

	public RequestThread(int queueSize, long waitTime, Reader<Query, Answer> reader)
	{
		super(queueSize);
		this.waitTime = waitTime;
		this.reader = reader;
	}

	@Override
	public void run()
	{
		Query query;
		Answer answer;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				query = super.dequeue();
				answer = this.reader.read(query);
				super.saveResponse(answer);
				super.disposeObject(query, answer);
			}
			try
			{
				super.holdOn(this.waitTime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
