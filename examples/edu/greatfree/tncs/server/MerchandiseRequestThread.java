package edu.greatfree.tncs.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;

import edu.greatfree.tncs.message.Merchandise;
import edu.greatfree.tncs.message.MerchandiseRequest;
import edu.greatfree.tncs.message.MerchandiseResponse;
import edu.greatfree.tncs.message.MerchandiseStream;

// Created: 05/01/2019, Bing Li
class MerchandiseRequestThread extends RequestQueue<MerchandiseRequest, MerchandiseStream, MerchandiseResponse>
{

	public MerchandiseRequestThread(int notificationQueueSize)
	{
		super(notificationQueueSize);
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
				request = this.getRequest();
				response = new MerchandiseResponse(new Merchandise("#001", "GreatFree Smartphone", "GF-01-12345", "This is a phone powered by GreatFree", 899.0f, 100, "GreatFree Co.", "Ground Shipping"));
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
