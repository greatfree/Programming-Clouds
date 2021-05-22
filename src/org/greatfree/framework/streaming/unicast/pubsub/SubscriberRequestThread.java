package org.greatfree.framework.streaming.unicast.pubsub;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.streaming.broadcast.pubsub.StreamRegistry;
import org.greatfree.framework.streaming.message.SubscriberRequest;
import org.greatfree.framework.streaming.message.SubscriberResponse;
import org.greatfree.framework.streaming.message.SubscriberStream;
import org.greatfree.util.Rand;

// Created: 03/23/2020, Bing Li
class SubscriberRequestThread extends RequestQueue<SubscriberRequest, SubscriberStream, SubscriberResponse>
{

	public SubscriberRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SubscriberStream request;
		SubscriberResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				response = new SubscriberResponse(StreamRegistry.PUBSUB().getAddress(Rand.getRandomListElement(StreamRegistry.PUBSUB().getSubscribers(request.getMessage().getPublisher(), request.getMessage().getTopic()))));
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
