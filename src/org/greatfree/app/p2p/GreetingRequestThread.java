package org.greatfree.app.p2p;

import java.io.IOException;

import org.greatfree.app.p2p.message.GreetingRequest;
import org.greatfree.app.p2p.message.GreetingResponse;
import org.greatfree.app.p2p.message.GreetingStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 08/19/2018, Bing Li
class GreetingRequestThread extends RequestQueue<GreetingRequest, GreetingStream, GreetingResponse>
{

	public GreetingRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		GreetingStream request;
		GreetingResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = new GreetingResponse("Fine! How are you?");
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				this.disposeMessage(request, response);
			}
			try
			{
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
