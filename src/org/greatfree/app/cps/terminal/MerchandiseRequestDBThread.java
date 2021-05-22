package org.greatfree.app.cps.terminal;

import java.io.IOException;

import org.greatfree.app.cps.message.MerchandiseRequest;
import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.app.cps.message.MerchandiseStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 08/14/2018, Bing Li
public class MerchandiseRequestDBThread extends RequestQueue<MerchandiseRequest, MerchandiseStream, MerchandiseResponse>
{

	public MerchandiseRequestDBThread(int maxTaskSize)
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
				response = new MerchandiseResponse("iPhoneX", 100);
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
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
