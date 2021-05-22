package org.greatfree.app.cs.twonode.server;

import java.io.IOException;

import org.greatfree.app.cs.twonode.message.MerchandiseRequest;
import org.greatfree.app.cs.twonode.message.MerchandiseResponse;
import org.greatfree.app.cs.twonode.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 07/27/2018, Bing Li
class MerchandiseRequestThread extends RequestQueue<MerchandiseRequest, MerchandiseStream, MerchandiseResponse>
{

	public MerchandiseRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		MerchandiseStream request;
		MerchandiseResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				System.out.println("Your query is received: " + request.getMessage().getQuery());
				response = new MerchandiseResponse("Android Smart Phone: Samsung, $200");
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
