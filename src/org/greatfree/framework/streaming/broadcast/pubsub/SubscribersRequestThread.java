package org.greatfree.framework.streaming.broadcast.pubsub;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.streaming.message.SubscribersRequest;
import org.greatfree.framework.streaming.message.SubscribersResponse;
import org.greatfree.framework.streaming.message.SubscribersStream;

// Created: 03/20/2020, Bing Li
class SubscribersRequestThread extends RequestQueue<SubscribersRequest, SubscribersStream, SubscribersResponse>
{

	public SubscribersRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SubscribersStream request;
		SubscribersResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				if (request.getMessage().isAll())
				{
					response = new SubscribersResponse(StreamRegistry.PUBSUB().getSubscribers(request.getMessage().getPublisher(), request.getMessage().getTopic()));
				}
				else
				{
					response = new SubscribersResponse(StreamRegistry.PUBSUB().getSubscriber(request.getMessage().getPublisher(), request.getMessage().getTopic()));
				}
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
