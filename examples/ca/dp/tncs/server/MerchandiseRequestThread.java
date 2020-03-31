package ca.dp.tncs.server;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

import ca.dp.tncs.message.Book;
import ca.dp.tncs.message.MerchandiseRequest;
import ca.dp.tncs.message.MerchandiseResponse;
import ca.dp.tncs.message.MerchandiseStream;

// Created: 02/22/2020, Bing Li
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
				response = new MerchandiseResponse(new Book("Java", "Mike", 2.45f));
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
