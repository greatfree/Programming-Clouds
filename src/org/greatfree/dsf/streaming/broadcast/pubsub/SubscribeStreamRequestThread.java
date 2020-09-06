package org.greatfree.dsf.streaming.broadcast.pubsub;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.streaming.message.SubscribeOutStream;
import org.greatfree.dsf.streaming.message.SubscribeStreamRequest;
import org.greatfree.dsf.streaming.message.SubscribeStreamResponse;

// Created: 03/19/2020, Bing Li
class SubscribeStreamRequestThread extends RequestQueue<SubscribeStreamRequest, SubscribeOutStream, SubscribeStreamResponse>
{

	public SubscribeStreamRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SubscribeOutStream request;
		SubscribeStreamResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new SubscribeStreamResponse(StreamRegistry.PUBSUB().subscribe(request.getMessage().getPublisher(), request.getMessage().getTopic(), request.getMessage().getSubscriber()));
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
