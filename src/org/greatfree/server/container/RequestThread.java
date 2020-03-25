package org.greatfree.server.container;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Request;
import org.greatfree.message.container.RequestStream;
import org.greatfree.util.ServerStatus;

// Created: 12/18/2018, Bing Li
class RequestThread extends RequestQueue<Request, RequestStream, ServerMessage>
{

	public RequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		RequestStream request;
		ServerMessage response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = ServiceProvider.CS().processRequest(request.getMessage());
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					ServerStatus.FREE().printException(e);
				}
				this.disposeMessage(request, response);
			}
			try
			{
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
	}
}
