package org.greatfree.app.cs.twonode.server;

import java.io.IOException;

import org.greatfree.app.cs.twonode.message.MerchandisePollRequest;
import org.greatfree.app.cs.twonode.message.MerchandisePollResponse;
import org.greatfree.app.cs.twonode.message.MerchandisePollStream;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

// Created: 07/31/2018, Bing Li
class MerchandisePollRequestThread extends RequestQueue<MerchandisePollRequest, MerchandisePollStream, MerchandisePollResponse>
{

	public MerchandisePollRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		MerchandisePollStream request;
		MerchandisePollResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new MerchandisePollResponse(MerchandiseDB.CS().isAvailable(request.getMessage().getMerchandise(), request.getMessage().getQuantity()));
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
