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
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				request = super.dequeue();
				
//				System.out.println("RequestThread: serverKey = " + super.getServerKey());
				
				response = ServiceProvider.CS().processRequest(super.getServerKey(), request.getMessage());

				// The sleeping aims to test the reading out feature. 03/29/2020, Bing Li
				/*
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException e1)
				{
					e1.printStackTrace();
				}
				*/
				try
				{
					super.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					ServerStatus.FREE().printException(e);
				}
				super.disposeMessage(request, response);
			}
			try
			{
				/*
				 * It is not necessary since the shutdown state is judged in the loop immediately. 12/01/2021, Bing Li
				 * I modified the code. If the thread needs to be shutdown, it is not necessary to keep it in the loops. 12/01/2021, Bing Li
				 */
				/*
				if (!super.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME))
				{
					return;
				}
				*/
				super.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
	}
}
