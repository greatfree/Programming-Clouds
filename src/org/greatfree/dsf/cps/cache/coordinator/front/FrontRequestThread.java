package org.greatfree.dsf.cps.cache.coordinator.front;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.coordinator.Coordinator;
import org.greatfree.dsf.cps.threetier.message.CoordinatorResponse;
import org.greatfree.dsf.cps.threetier.message.FrontRequest;
import org.greatfree.dsf.cps.threetier.message.FrontResponse;
import org.greatfree.dsf.cps.threetier.message.FrontStream;
import org.greatfree.exceptions.RemoteReadException;

// Created: 07/06/2018, Bing Li
public class FrontRequestThread extends RequestQueue<FrontRequest, FrontStream, FrontResponse>
{

	public FrontRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		FrontStream request;
		FrontResponse response;
		CoordinatorResponse terminalResponse;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				try
				{
					System.out.println("The query from front end: " + request.getMessage().getQuery());
					terminalResponse = Coordinator.CPS().query(request.getMessage().getQuery());
					System.out.println("The answer from terminal server: " + terminalResponse.getAnswer());
					response = new FrontResponse(terminalResponse.getAnswer());
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
				{
					e.printStackTrace();
				}
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
