package org.greatfree.app.business.dip.cs.multinode.server;

import java.io.IOException;

import org.greatfree.chat.message.cs.business.CheckSalesRequest;
import org.greatfree.chat.message.cs.business.CheckSalesResponse;
import org.greatfree.chat.message.cs.business.CheckSalesStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 12/22/2017, Bing Li
public class CheckSalesThread extends RequestQueue<CheckSalesRequest, CheckSalesStream, CheckSalesResponse>
{

	public CheckSalesThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		CheckSalesStream request;
		CheckSalesResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = new CheckSalesResponse(Businesses.getVendorNames(), Businesses.getSales());
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
					this.disposeMessage(request, response);
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
