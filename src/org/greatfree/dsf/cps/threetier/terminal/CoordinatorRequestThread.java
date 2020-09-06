package org.greatfree.dsf.cps.threetier.terminal;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.threetier.message.CoordinatorRequest;
import org.greatfree.dsf.cps.threetier.message.CoordinatorResponse;
import org.greatfree.dsf.cps.threetier.message.CoordinatorStream;

// Created: 07/07/2018, Bing Li
class CoordinatorRequestThread extends RequestQueue<CoordinatorRequest, CoordinatorStream, CoordinatorResponse>
{

	public CoordinatorRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		CoordinatorStream request;
		CoordinatorResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				System.out.println("Coordinator query: " + request.getMessage().getQuery());
				response = new CoordinatorResponse("Terminal server says: Cloud programming is the technique to implement tough systems!");
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
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 02/15/2016, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
