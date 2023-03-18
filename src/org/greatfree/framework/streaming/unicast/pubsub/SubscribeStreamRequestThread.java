package org.greatfree.framework.streaming.unicast.pubsub;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.streaming.Stream;
import org.greatfree.framework.streaming.broadcast.pubsub.StreamRegistry;
import org.greatfree.framework.streaming.message.SubscribeNotification;
import org.greatfree.framework.streaming.message.SubscribeOutStream;
import org.greatfree.framework.streaming.message.SubscribeStreamRequest;
import org.greatfree.framework.streaming.message.SubscribeStreamResponse;
import org.greatfree.util.IPAddress;

// Created: 03/23/2020, Bing Li
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
		boolean isSucceeded;
		IPAddress ip;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.dequeue();
				isSucceeded = StreamRegistry.PUBSUB().subscribe(request.getMessage().getPublisher(), request.getMessage().getTopic(), request.getMessage().getSubscriber());
				if (isSucceeded)
				{
					try
					{
						ip = PubSubServer.UNI_STREAM().getAddress(request.getMessage().getSubscriber());
						StreamRegistry.PUBSUB().addAddress(request.getMessage().getSubscriber(), ip);
						PubSubServer.UNI_STREAM().subscribe(new SubscribeNotification(Stream.generateKey(request.getMessage().getPublisher(), request.getMessage().getTopic()), request.getMessage().getSubscriber(), ip));
					}
					catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
					{
						e.printStackTrace();
					}
				}
				response = new SubscribeStreamResponse(isSucceeded);
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
