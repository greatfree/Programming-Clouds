package org.greatfree.app.business.dip.cs.multinode.server;

import java.io.IOException;

import org.greatfree.chat.message.cs.business.CheckCartRequest;
import org.greatfree.chat.message.cs.business.CheckCartResponse;
import org.greatfree.chat.message.cs.business.CheckCartStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 12/11/2017, Bing Li
public class CheckCartThread extends RequestQueue<CheckCartRequest, CheckCartStream, CheckCartResponse>
{

	public CheckCartThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		CheckCartStream request;
		CheckCartResponse response;
		// Check whether the thread is shutdown or not. 12/05/2017, Bing Li
		while (!this.isShutdown())
		{
			// Check whether the message queue of the thread is empty or not. 12/05/2017, Bing Li
			while (!this.isEmpty())
			{
				// Get the request out from the message queue. 12/05/2017, Bing Li
				request = this.getRequest();
				// Create the response. 12/05/2017, Bing Li
				response = new CheckCartResponse(Businesses.getMerchandises(request.getMessage().getVendorKey(), request.getMessage().getCustomerKey()));
				try
				{
					// Respond to the customer. 12/05/2017, Bing Li
					this.respond(request.getOutStream(), request.getLock(), response);
					// Dispose the messages, including the request and the response. 12/05/2017, Bing Li
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
