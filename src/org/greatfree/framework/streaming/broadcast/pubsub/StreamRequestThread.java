package org.greatfree.framework.streaming.broadcast.pubsub;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.streaming.message.OutStream;
import org.greatfree.framework.streaming.message.StreamRequest;
import org.greatfree.framework.streaming.message.StreamResponse;

// Created: 03/21/2020, Bing Li
public class StreamRequestThread extends RequestQueue<StreamRequest, OutStream, StreamResponse>
{

	public StreamRequestThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		OutStream request;
		StreamResponse response;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				response = new StreamResponse(StreamRegistry.PUBSUB().getAllStreams());
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
